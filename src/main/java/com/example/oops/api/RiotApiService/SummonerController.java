package com.example.oops.api.RiotApiService;


import com.example.oops.api.RiotApiService.dto.PlayerDetailsDTO;
import com.example.oops.common.error.ErrorCode;
import com.example.oops.common.error.OopsException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class SummonerController {

    private final RiotDataService riotDataService;
    private final RiotApiService riotApiService;
    private final String SOLO_RANK_TYPE = "RANKED_SOLO_5x5";
    private final String FLEX_RANK_TYPE = "RANKED_FLEX_SR"; // 자유 랭크 상수
    private final int MATCH_COUNT = 5;

    /**
     * Riot ID로 소환사 기본 정보, 랭크, 매치 기록을 통합하여 조회합니다.
     * 엔드포인트: GET /api/v1/account/{gameName}/{tagLine}
     */
    @GetMapping("/{gameName}/{tagLine}")
    public Mono<SummonerProfileDTO> getFullSummonerProfile(@PathVariable String gameName, @PathVariable String tagLine) {

        // 1. PUUID 획득 Mono (다른 모든 체인의 기반)
        Mono<String> puuidMono = riotApiService.getAccountByRiotId(gameName, tagLine)
                .map(accountDto -> accountDto.getPuuid());
        // PUUID를 찾지 못하면 RiotApiService의 applyErrorHandler에서 OopsException이 발생함.
        // switchIfEmpty는 Riot API가 204 No Content를 반환할 때를 대비하지만,
        // Riot Account API는 404를 반환하므로 생략 가능합니다.

        // A. 프로필 및 랭크 정보 조회 체인 (2, 3단계 통합)
        Mono<SummonerDTO> profileAndRankMono = puuidMono.flatMap(puuid ->
                riotApiService.getSummonerByPuuid(puuid)
                        .flatMap(summonerDto -> {
                            // 이름/태그 주입 (이름 누락 문제 해결)
                            summonerDto.setName(gameName);
                            summonerDto.setTag(tagLine);

                            // 3단계: 랭크 정보 통합
                            return riotApiService.getLeagueEntriesByPuuid(summonerDto.getPuuid())
                                    .map(leagueEntries -> {
                                        for (LeagueEntryDTO entry : leagueEntries) {
                                            if (SOLO_RANK_TYPE.equals(entry.getQueueType())) {
                                                summonerDto.setSoloRank(entry);
                                            } else if (FLEX_RANK_TYPE.equals(entry.getQueueType())) {
                                                summonerDto.setFlexRank(entry);
                                            }
                                        }
                                        return summonerDto;
                                    })
                                    .defaultIfEmpty(summonerDto); // 랭크 정보가 없으면 기본 DTO 반환
                        })
        );

        // B. 매치 기록 조회 체인 (4, 5단계 통합)
        Mono<List<MatchDetailDTO>> matchesMono = puuidMono.flatMap(puuid ->
                // 4단계: 매치 ID 목록 조회
                riotApiService.getMatchIdsByPuuid(puuid, MATCH_COUNT)
                        // 5단계: 매치 ID 목록을 Flux로 변환하고, 각 ID에 대해 상세 정보 비동기 호출
                        .flatMapMany(Flux::fromIterable)
                        .flatMap(riotApiService::getMatchDetailsByMatchId)
                        // 상세 정보가 모두 도착하면 리스트로 다시 모음
                        .collectList()
        );

        // C. 챔피언 마스터리 조회 모노 (이전 논의에서 누락되었던 부분)
        Mono<List<ChampionMasteryDTO>> masteriesMono = puuidMono
                .flatMap(riotApiService::getChampionMasteriesByPuuid)
                .onErrorReturn(List.of()); // 마스터리 조회 실패 시 빈 리스트 반환 (치명적 오류 아님)


        // D. 최종 통합: A, B, C의 결과를 Mono.zip으로 합쳐 최종 DTO 반환
        return Mono.zip(profileAndRankMono, matchesMono, masteriesMono)
                .map(tuple -> {
                    SummonerProfileDTO finalDto = new SummonerProfileDTO();
                    finalDto.setProfile(tuple.getT1());     // T1: SummonerDTO (프로필 + 랭크)
                    finalDto.setMatchHistory(tuple.getT2());// T2: List<MatchDetailDTO> (매치 기록)
                    finalDto.setMasteries(tuple.getT3());   // T3: List<ChampionMasteryDTO> (마스터리)
                    return finalDto;
                })
                .doOnError(e -> System.err.println("소환사 전체 프로필 조회 실패: " + e.getMessage()))
                .onErrorResume(e -> {
                    // 💡 핵심 수정: OopsException을 잡아 ErrorCode를 기반으로 응답합니다.
                    if (e instanceof OopsException) {
                        OopsException oe = (OopsException) e;
                        ErrorCode errorCode = oe.getErrorCode();

                        // 404 NOT FOUND 에러에 대한 사용자 친화적 메시지 처리
                        if (errorCode == ErrorCode.RIOT_API_NOT_FOUND) {
                            throw new ResponseStatusException(
                                    errorCode.getStatus(),
                                    "입력하신 Riot ID (GameName: " + gameName + ", TagLine: " + tagLine + ")를 찾을 수 없습니다."
                            );
                        }

                        // 그 외의 모든 OopsException (429, 500 등) 처리
                        throw new ResponseStatusException(
                                errorCode.getStatus(),
                                errorCode.getMessage() // ErrorCode에 정의된 메시지를 사용
                        );
                    }

                    // 예상치 못한 시스템 오류 처리
                    throw new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "서버에서 소환사 정보를 처리하는 중 알 수 없는 오류가 발생했습니다.",
                            e
                    );
                });
    }
    @GetMapping("/player-details")
    public Mono<PlayerDetailsDTO> getPlayerDetails(
            @RequestParam String gameName,
            @RequestParam String tagLine
    ) {
        return riotDataService.getPlayerDetails(gameName, tagLine);
    }
}