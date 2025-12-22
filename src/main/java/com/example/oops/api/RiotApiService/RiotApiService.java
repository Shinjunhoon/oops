package com.example.oops.api.RiotApiService;


import com.example.oops.common.error.ErrorCode;
import com.example.oops.common.error.OopsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;


@Service
@Slf4j
public class RiotApiService {

    private final String apiKey;
    private final WebClient krWebClient;
    private final WebClient asiaWebClient;

    public RiotApiService(WebClient.Builder webClientBuilder,
                          @Value("${riot.api-key}") String apiKey, // 💡 API 키를 생성자로 주입
                          @Value("${riot.base-url.kr}") String krBaseUrl,
                          @Value("${riot.base-url.asia}") String asiaBaseUrl) {

        this.apiKey = apiKey;
        this.krWebClient = webClientBuilder.baseUrl(krBaseUrl).build();
        this.asiaWebClient = webClientBuilder.baseUrl(asiaBaseUrl).build();
    }

    /**
     * Riot API 응답에 대한 공통 에러 핸들링 로직을 적용합니다.
     * WebClient.ResponseSpec이 에러 상태 코드(4xx, 5xx)를 반환할 경우,
     * 적절한 ErrorCode를 가진 OopsException으로 변환합니다.
     */
    private WebClient.ResponseSpec applyErrorHandler(WebClient.ResponseSpec responseSpec) {
        return responseSpec.onStatus(status -> status.isError(), clientResponse ->
                clientResponse.bodyToMono(String.class)
                        .flatMap(body -> {
                            HttpStatus status = (HttpStatus) clientResponse.statusCode();

                            ErrorCode errorCode;
                            if (status == HttpStatus.NOT_FOUND) {
                                errorCode = ErrorCode.RIOT_API_NOT_FOUND;
                            } else if (status == HttpStatus.TOO_MANY_REQUESTS) {
                                errorCode = ErrorCode.RIOT_API_RATE_LIMIT_EXCEEDED;
                            } else {
                                // 4xx, 5xx 에러
                                errorCode = ErrorCode.RIOT_API_INTERNAL_SERVER_ERROR;
                            }
                            log.error("Riot API Error | Status: {} | Body: {}", status, body);
                            return Mono.error(new OopsException(errorCode));
                        })
        );
    }

    /**
     * API 호출의 응답 Mono에 공통 재시도 로직을 적용합니다.
     * - 최대 3회 재시도하며, 시도 간 500ms부터 지수적으로 대기 시간이 증가합니다 (백오프).
     * - OopsException 중 NOT_FOUND 에러는 영구적 에러로 간주하여 재시도하지 않고 바로 실패합니다.
     */
    private <T> Mono<T> applyRetryLogic(Mono<T> mono) {
        return mono.retryWhen(Retry.backoff(3, Duration.ofMillis(500))
                // 특정 오류(404 등)는 재시도하지 않도록 필터링
                .filter(throwable -> !(throwable instanceof OopsException
                        && ((OopsException) throwable).getErrorCode() == ErrorCode.RIOT_API_NOT_FOUND))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                    // 모든 재시도가 실패했을 때 최종적으로 예외를 던집니다.
                    log.error("Riot API Call failed after max retries: {}", retrySignal.failure().getMessage());
                    return retrySignal.failure();
                })
        );
    }

    /**
     * WebClient 호출, 에러 핸들러 적용, Mono 변환 과정을 캡슐화합니다.
     */
    private <T> Mono<T> createResponseMono(WebClient webClient, String path, ParameterizedTypeReference<T> typeReference) {
        return applyErrorHandler(webClient.get()
                .uri(uriBuilder -> uriBuilder.path(path).queryParam("api_key", apiKey).build())
                .retrieve())
                .bodyToMono(typeReference);
    }

    //---------------------------------------------------------------------
    // 💡 아래의 모든 API 호출 메서드에서 'createResponseMono'와 'applyRetryLogic'을 사용합니다.
    //---------------------------------------------------------------------

    // 1단계: Riot ID로 AccountDTO 조회 (ASIA Route)
    public Mono<AccountDTO> getAccountByRiotId(String gameName, String tagLine) {
        String path = String.format("/riot/account/v1/accounts/by-riot-id/%s/%s", gameName, tagLine);

        // 1. Mono 생성 + 에러 핸들링
        Mono<AccountDTO> responseMono = createResponseMono(asiaWebClient, path, ParameterizedTypeReference.forType(AccountDTO.class));

        // 2. 재시도 로직 적용 후 반환
        return applyRetryLogic(responseMono);
    }

    // 2단계: PUUID로 SummonerDTO 조회 (KR Route)
    public Mono<SummonerDTO> getSummonerByPuuid(String puuid) {
        String path = String.format("/lol/summoner/v4/summoners/by-puuid/%s", puuid);

        Mono<SummonerDTO> responseMono = createResponseMono(krWebClient, path, ParameterizedTypeReference.forType(SummonerDTO.class));

        return applyRetryLogic(responseMono);
    }

    // 3단계: PUUID로 티어 정보 조회 (KR Route)
    public Mono<List<LeagueEntryDTO>> getLeagueEntriesByPuuid(String puuid) {
        String path = String.format("/lol/league/v4/entries/by-puuid/%s", puuid);

        Mono<List<LeagueEntryDTO>> responseMono = createResponseMono(krWebClient, path, new ParameterizedTypeReference<List<LeagueEntryDTO>>() {});

        return applyRetryLogic(responseMono);
    }

    // 4단계: PUUID로 매치 ID 목록 조회 (ASIA Route)
    public Mono<List<String>> getMatchIdsByPuuid(String puuid, int count) {
        String path = String.format("/lol/match/v5/matches/by-puuid/%s/ids", puuid);

        // 매치 목록은 쿼리 파라미터가 추가되므로, URI 생성을 인라인으로 처리합니다. (혹은 별도의 오버로드 메서드 필요)
        Mono<List<String>> responseMono = applyErrorHandler(asiaWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path).queryParam("start", 0).queryParam("count", count)
                        .queryParam("api_key", apiKey).build())
                .retrieve())
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {});

        return applyRetryLogic(responseMono);
    }

    // 5단계: 매치 ID로 매치 상세 정보 조회 (ASIA Route)
    public Mono<MatchDetailDTO> getMatchDetailsByMatchId(String matchId) {
        String path = String.format("/lol/match/v5/matches/%s", matchId);

        Mono<MatchDetailDTO> responseMono = createResponseMono(asiaWebClient, path, ParameterizedTypeReference.forType(MatchDetailDTO.class));

        return applyRetryLogic(responseMono);
    }

    // 6단계: PUUID로 챔피언 마스터리 정보 조회 (KR Route)
    public Mono<List<ChampionMasteryDTO>> getChampionMasteriesByPuuid(String puuid) {
        String path = String.format("/lol/champion-mastery/v4/champion-masteries/by-puuid/%s", puuid);

        Mono<List<ChampionMasteryDTO>> responseMono = createResponseMono(krWebClient, path, new ParameterizedTypeReference<List<ChampionMasteryDTO>>() {});

        return applyRetryLogic(responseMono);
    }

    public Mono<Integer> getChampionIdByPuuid(String puuid) {
        String path = String.format(
                "/lol/champion-mastery/v4/champion-masteries/by-puuid/%s",
                puuid
        );

        Mono<List<ChampionMasteryDTO>> responseMono =
                createResponseMono(krWebClient, path,
                        new ParameterizedTypeReference<List<ChampionMasteryDTO>>() {});

        return applyRetryLogic(responseMono)
                .map(list -> list.get(0).getChampionId()); // 가장 많이 한 챔피언
    }
}