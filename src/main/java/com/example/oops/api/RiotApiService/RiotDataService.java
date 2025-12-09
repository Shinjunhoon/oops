package com.example.oops.api.RiotApiService;
import com.example.oops.api.RiotApiService.dto.PlayerDetailsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiotDataService {

    private final RiotApiService riotApiService;
    private final DDragonService dDragonService;

    // 한국 시간대 (KST) 포매터
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 💡 최종 사용자에게 제공할 데이터를 통합하고 가공합니다.
     */
    public Mono<PlayerDetailsDTO> getPlayerDetails(String gameName, String tagLine) {
        // 1단계: AccountDTO를 통해 PUUID 획득 (ASIA Route)
        return riotApiService.getAccountByRiotId(gameName, tagLine)
                .flatMap(account -> {
                    String puuid = account.getPuuid();

                    // 2단계: PUUID를 이용한 나머지 데이터 병렬 호출
                    // SummonerDTO, 랭크 정보, 마스터리, 매치 ID 목록 (KR, ASIA Route)
                    Mono<SummonerDTO> summonerMono = riotApiService.getSummonerByPuuid(puuid);
                    Mono<List<LeagueEntryDTO>> rankMono = riotApiService.getLeagueEntriesByPuuid(puuid);
                    Mono<List<ChampionMasteryDTO>> masteryMono = riotApiService.getChampionMasteriesByPuuid(puuid);

                    // 최근 매치 5개 가져오기
                    Mono<List<MatchDetailDTO>> latestMatchesMono = riotApiService.getMatchIdsByPuuid(puuid, 10)
                            .flatMapMany(matchIds -> {
                                if (matchIds.isEmpty()) return Flux.empty();
                                // 각 매치 ID에 대해 상세 정보를 병렬 호출
                                return Flux.fromIterable(matchIds)
                                        .flatMap(riotApiService::getMatchDetailsByMatchId);
                            })
                            .collectList()
                            // 🌟 수정된 부분: List<MatchDetailDTO>를 받은 후 정렬을 수행합니다. 🌟
                            .map(matches -> matches.stream()
                                    // gameCreation (타임스탬프) 기준으로 내림차순 정렬 (최신순)
                                    .sorted(Comparator.comparingLong((MatchDetailDTO m) -> m.getInfo().getGameCreation()).reversed())
                                    .collect(Collectors.toList()));

                    // 모든 Mono의 결과를 하나로 묶어 기다립니다. (병렬 처리)
                    // latestMatchMono 대신 latestMatchesMono를 사용
                    return Mono.zip(summonerMono, rankMono, masteryMono, latestMatchesMono)
                            .map(tuple -> {
                                // 튜플에서 결과를 추출
                                SummonerDTO summoner = tuple.getT1();
                                List<LeagueEntryDTO> ranks = tuple.getT2();
                                List<ChampionMasteryDTO> masteries = tuple.getT3();
                                List<MatchDetailDTO> latestMatches = tuple.getT4(); // <-- List<MatchDetailDTO>로 변경

                                // DDragonService를 사용하여 DTO 변환 및 가공
                                // buildPlayerDetailsDTO 메서드 시그니처 변경 필요 (MatchDetailDTO -> List<MatchDetailDTO>)
                                return buildPlayerDetailsDTO(account, summoner, ranks, masteries, latestMatches);
                            });
                });
    }

    /**
     * 최종 DTO를 빌드하는 변환 로직 (DDragonService 사용)
     */
    private PlayerDetailsDTO buildPlayerDetailsDTO(
            AccountDTO account,
            SummonerDTO summoner,
            List<LeagueEntryDTO> ranks,
            List<ChampionMasteryDTO> masteries,
            List<MatchDetailDTO> recentMatches
    ) {
        // 1. 랭크 정보 변환
        PlayerDetailsDTO.LeagueRankInfo soloRank = ranks.stream()
                .filter(r -> "RANKED_SOLO_5x5".equals(r.getQueueType()))
                .findFirst()
                .map(this::toLeagueRankInfo)
                .orElse(null);

        PlayerDetailsDTO.LeagueRankInfo flexRank = ranks.stream()
                .filter(r -> "RANKED_FLEX_SR".equals(r.getQueueType()))
                .findFirst()
                .map(this::toLeagueRankInfo)
                .orElse(null);

        // 2. 챔피언 숙련도 변환
        List<PlayerDetailsDTO.MasteryInfo> processedMasteries = masteries.stream()
                .limit(5) // 상위 5개만 처리
                .map(this::toMasteryInfo)
                .collect(Collectors.toList());
        // 3. 매치 정보 변환
        List<PlayerDetailsDTO.MatchInfo> processedMatches = recentMatches.stream()
                .map(this::toMatchInfo)
                .collect(Collectors.toList());


        // 4. 최종 DTO 빌드 (프로필 정보 포함)
        return PlayerDetailsDTO.builder()
                .gameName(account.getGameName())
                .tagLine(account.getTagLine())
                .puuid(summoner.getPuuid())
                .profileIconId(summoner.getProfileIconId())
                // 💡 DDragonService 적용: 아이콘 ID -> URL 변환
                .profileIconUrl(dDragonService.getProfileIconUrl(summoner.getProfileIconId()))
                .summonerLevel(summoner.getSummonerLevel())
                // 💡 Timestamp 변환
                .lastRevisionDateKr(timestampToKst(summoner.getRevisionDate()))
                .soloRank(soloRank)
                .flexRank(flexRank)
                .masteries(processedMasteries)
                .recentMatches(processedMatches)
                .build();
    }

    /* --- 내부 변환 헬퍼 메서드 --- */

    private PlayerDetailsDTO.LeagueRankInfo toLeagueRankInfo(LeagueEntryDTO dto) {
        int totalGames = dto.getWins() + dto.getLosses();
        double winRate = totalGames > 0 ? (double) dto.getWins() / totalGames * 100 : 0.0;
        return PlayerDetailsDTO.LeagueRankInfo.builder()
                .queueType(dto.getQueueType())
                .tier(dto.getTier())
                .rank(dto.getRank())
                .leaguePoints(dto.getLeaguePoints())
                .wins(dto.getWins())
                .losses(dto.getLosses())
                .winRate(Math.round(winRate * 10.0) / 10.0) // 소수점 첫째자리까지
                .build();
    }

    private PlayerDetailsDTO.MasteryInfo toMasteryInfo(ChampionMasteryDTO dto) {
        String championNameEn = dDragonService.getChampionNameById(dto.getChampionId());
        return PlayerDetailsDTO.MasteryInfo.builder()
                // 💡 DDragonService 적용: ID -> 이미지 URL
                .championImageUrl(dDragonService.getChampionImageUrl(championNameEn))
                // 💡 DDragonService 적용: ID -> 한글 이름 (DDragon champion.json에 있는 name 필드 사용)
                .championNameKr(dDragonService.getChampionNameById(Integer.parseInt(String.valueOf(dto.getChampionId()))))
                .level(dto.getChampionLevel())
                .points(dto.getChampionPoints())
                // 💡 Timestamp 변환
                .lastPlayTimeKr(timestampToKst(dto.getLastPlayTime()))
                .build();
    }

    private PlayerDetailsDTO.MatchInfo toMatchInfo(MatchDetailDTO dto) {
        return PlayerDetailsDTO.MatchInfo.builder()
                .matchId(dto.getMetadata().getMatchId())
                .gameDurationSeconds(dto.getInfo().getGameDuration())
                // 💡 Timestamp 변환
                .gameCreationTimeKr(timestampToKst(dto.getInfo().getGameCreation()))
                .queueTypeKr(mapQueueIdToName(dto.getInfo().getQueueId()))
                // 참가자 목록 변환
                .participants(dto.getInfo().getParticipants().stream()
                        .map(this::toParticipantInfo)
                        .collect(Collectors.toList()))
                .build();
    }

    private PlayerDetailsDTO.ParticipantInfo toParticipantInfo(ParticipantDTO dto) {
        // 챔피언 영문 이름은 Riot API에서 제공됨 (변환 필요 없음)
        String championNameEn = dto.getChampionName();

        // 아이템 정보 (item0 ~ item6)
        List<PlayerDetailsDTO.ItemInfo> items = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> {
                    try {
                        // 리플렉션을 사용하여 getItem0, getItem1 등의 메서드를 동적으로 호출
                        int itemId = (int) dto.getClass().getDeclaredMethod("getItem" + i).invoke(dto);
                        return PlayerDetailsDTO.ItemInfo.builder()
                                // 💡 DDragonService 적용: ID -> 한글 이름
                                .nameKr(dDragonService.getItemNameById(itemId))
                                // 💡 DDragonService 적용: ID -> 이미지 URL
                                .imageUrl(dDragonService.getItemImageUrl(itemId))
                                .isEmpty(itemId == 0)
                                .build();
                    } catch (Exception e) {
                        log.error("Failed to get item info for item slot {}", i, e);
                        return PlayerDetailsDTO.ItemInfo.builder().isEmpty(true).build();
                    }
                })
                .filter(item -> item != null && !item.isEmpty()) // 빈 슬롯이 아닌 아이템만 표시
                .collect(Collectors.toList()); // 정렬은 따로 하지 않음

        // 소환사 주문 정보
        PlayerDetailsDTO.SpellInfo spell1 = PlayerDetailsDTO.SpellInfo.builder()
                .id(dto.getSummoner1Id())
                .nameKr(dDragonService.getSpellNameById(dto.getSummoner1Id()))
                .imageUrl(dDragonService.getSpellImageUrl(dto.getSummoner1Id()))
                .build();

        PlayerDetailsDTO.SpellInfo spell2 = PlayerDetailsDTO.SpellInfo.builder()
                .id(dto.getSummoner2Id())
                .nameKr(dDragonService.getSpellNameById(dto.getSummoner2Id()))
                .imageUrl(dDragonService.getSpellImageUrl(dto.getSummoner2Id()))
                .build();

        // KDA 계산 (0 나누기 방지)
        String kda = dto.getDeaths() == 0
                ? String.format("%.2f:1", (double) (dto.getKills() + dto.getAssists()))
                : String.format("%.2f:1", (double) (dto.getKills() + dto.getAssists()) / dto.getDeaths());


        // 💡 룬(Perks) 정보 추출 및 변환 (이미지 URL 추가)
        PlayerDetailsDTO.RuneTreeInfo mainRune = null;
        PlayerDetailsDTO.RuneTreeInfo subRune = null;

        if (dto.getPerks() != null && dto.getPerks().getStyles() != null) {
            List<com.example.oops.api.RiotApiService.PerkStyleDto> styles = dto.getPerks().getStyles();

            // 주 룬 트리 (styles[0])에서 핵심 룬을 추출하여 mainRune에 담습니다.
            if (styles.size() > 0) {
                com.example.oops.api.RiotApiService.PerkStyleDto mainStyle = styles.get(0);

                // 🎯 핵심 룬은 selections 리스트의 첫 번째 아이템입니다.
                if (mainStyle.getSelections() != null && !mainStyle.getSelections().isEmpty()) {

                    // 💡 1. 핵심 룬 ID (Keystone Rune ID) 추출
                    int keystoneId = mainStyle.getSelections().get(0).getPerk();

                    mainRune = PlayerDetailsDTO.RuneTreeInfo.builder()
                            // 룬 트리 ID(8000) 대신 핵심 룬 ID(8005)를 저장합니다.
                            .styleId(keystoneId)
                            // 핵심 룬 ID로 이름 및 이미지 조회 (예: '집중 공격')
                            .name(dDragonService.getRuneNameById(keystoneId))
                            .imageUrl(dDragonService.getRuneImageUrl(keystoneId))
                            .build();
                }
            }

            // 보조 룬 트리 (styles[1]) 정보를 추출하여 subRune에 담습니다.
            if (styles.size() > 1) {
                com.example.oops.api.RiotApiService.PerkStyleDto subStyle = styles.get(1);

                // 💡 2. 보조 룬 트리 ID (Style ID) 추출
                subRune = PlayerDetailsDTO.RuneTreeInfo.builder()
                        .styleId(subStyle.getStyle())
                        // 보조 룬 트리 ID로 이름 및 이미지 조회 (예: '영감')
                        .name(dDragonService.getRuneNameById(subStyle.getStyle()))
                        .imageUrl(dDragonService.getRuneImageUrl(subStyle.getStyle()))
                        .build();
            }
        }

        return PlayerDetailsDTO.ParticipantInfo.builder()
                .riotIdGameName(dto.getRiotIdGameName())
                .riotIdTagline(dto.getRiotIdTagline())
                .win(dto.isWin())
                .kills(dto.getKills())
                .deaths(dto.getDeaths())
                .assists(dto.getAssists())
                .kda(kda)
                .championName(championNameEn)
                .totalDamageDealtToChampions(dto.getTotalDamageDealtToChampions()) // 🌟 추가
                .totalDamageTaken(dto.getTotalDamageTaken())                       // 🌟 추가
                .cs(dto.getTotalMinionsKilled()+dto.getNeutralMinionsKilled())
                .championImageUrl(dDragonService.getChampionImageUrl(championNameEn))
                .items(items)
                .spell1(spell1)
                .spell2(spell2)
                // 💡 룬 정보 추가
                .mainRuneTree(mainRune)
                .subRuneTree(subRune)
                .build();
    }

    /* --- 유틸리티 메서드 --- */

    private String timestampToKst(long timestampMs) {
        // Riot API는 밀리초(ms) 단위의 Timestamp를 사용합니다.
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampMs), ZoneId.of("Asia/Seoul"))
                .format(DATE_FORMATTER);
    }

    private String mapQueueIdToName(String queueId) {
        int qId;
        try {
            qId = Integer.parseInt(queueId);
        } catch (NumberFormatException e) {
            return "기타 큐 (" + queueId + ")";
        }

        return switch (qId) {
            case 420 -> "솔로 랭크";
            case 440 -> "자유 랭크";
            case 430 -> "일반 게임";
            case 450 -> "무작위 총력전";
            default -> "기타 큐 (" + queueId + ")";
        };
    }
}