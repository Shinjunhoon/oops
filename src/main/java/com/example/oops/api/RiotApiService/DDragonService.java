package com.example.oops.api.RiotApiService;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// DDragon 데이터 로드 및 조회를 담당하는 서비스
@Service
@Slf4j
public class DDragonService {
    private final WebClient webClient;
    private final Duration TIMEOUT = Duration.ofSeconds(10); // 로드 타임아웃 설정

    // 현재 롤 게임 버전
    @Getter
    private String latestVersion;

    // 챔피언 ID -> 영문 이름 캐시 (예: 10 -> "JarvanIV")
    private final Map<String, String> championIdToName = new HashMap<>();

    // 챔피언 영문 이름 -> 이미지 정보 캐시
    private final Map<String, JsonNode> championNameToImage = new HashMap<>();

    // 아이템 ID -> 아이템 정보 캐시
    private final Map<String, JsonNode> itemIdToInfo = new HashMap<>();

    // 소환사 주문 (스펠) ID -> 정보 캐시 (key는 숫자 ID, value는 JsonNode)
    private final Map<String, JsonNode> spellIdToInfo = new HashMap<>();

    // --- 🎯 새로 추가된 부분 (1/3): 룬 ID 캐시 필드 ---
    // 룬 ID (Int) -> 룬 정보 캐시
    private final Map<Integer, JsonNode> runeIdToInfo = new HashMap<>();
    // ----------------------------------------------------


    public DDragonService(WebClient.Builder webClientBuilder,
                          @Value("${ddragon.base-url}") String ddragonBaseUrl) {

        // --- 🎯핵심 수정: WebClient의 메모리 버퍼 크기를 5MB로 늘립니다. ---
        final int MAX_BUFFER_SIZE = 5 * 1024 * 1024; // 5MB

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(clientCodecConfigurer ->
                        clientCodecConfigurer.defaultCodecs().maxInMemorySize(MAX_BUFFER_SIZE))
                .build();

        this.webClient = webClientBuilder
                .baseUrl(ddragonBaseUrl)
                .exchangeStrategies(strategies) // 늘어난 버퍼 설정 적용
                .build();
    }

    /**
     * Spring 애플리케이션 시작 시 모든 정적 데이터를 동기적으로 로드합니다.
     * WebClient.block()을 사용하여 데이터 로드가 완료될 때까지 애플리케이션 시작을 대기시킵니다.
     */
    @PostConstruct
    public void init() {
        log.info("DDragon 데이터 로드 시작...");

        try {
            // 1. 최신 버전 동기적으로 가져오기
            this.latestVersion = getLatestVersion().block(TIMEOUT);

            if (this.latestVersion == null) {
                throw new IllegalStateException("Failed to retrieve latest DDragon version.");
            }

            // 2. 모든 정적 데이터 동기적으로 로드 (룬 로직 포함)
            loadAllStaticData(this.latestVersion).block(TIMEOUT);

            log.info("DDragon 데이터 로드 완료. Version: {}. Loaded Champions: {}, Items: {}, Spells: {}, Runes: {}",
                    this.latestVersion, championIdToName.size(), itemIdToInfo.size(), spellIdToInfo.size(), runeIdToInfo.size());

        } catch (Exception e) {
            log.error("DDragon 데이터 로드 실패: 애플리케이션 시작에 영향을 줄 수 있습니다. {}", e.getMessage(), e);
            // 필요에 따라 초기화 실패 시 시스템 종료 또는 폴백 로직 추가 가능
        }
    }

    private Mono<String> getLatestVersion() {
        // [0] 번째 인덱스에 최신 버전 정보가 있습니다.
        return webClient.get().uri("/api/versions.json")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(versions -> versions.get(0).asText());
    }

    private Mono<Void> loadAllStaticData(String version) {
        Mono<Void> championMono = loadChampions(version);
        Mono<Void> itemMono = loadItems(version);
        Mono<Void> spellMono = loadSpells(version);

        // --- 🎯 새로 추가된 부분 (2/3): 룬 로드 로직을 병렬 작업에 포함 ---
        Mono<Void> runeMono = loadRunes(version);

        // 모든 로드가 병렬로 완료될 때까지 기다립니다.
        return Mono.when(championMono, itemMono, spellMono, runeMono);
        // ------------------------------------------------------------------
    }

    private Mono<Void> loadChampions(String version) {
        return webClient.get().uri(String.format("/cdn/%s/data/ko_KR/champion.json", version))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(root -> root.get("data"))
                .doOnNext(data -> {
                    data.fields().forEachRemaining(entry -> {
                        String name = entry.getKey();
                        String id = entry.getValue().get("key").asText();

                        championIdToName.put(id, name);
                        championNameToImage.put(name, entry.getValue().get("image"));
                    });
                })
                .then();
    }

    private Mono<Void> loadItems(String version) {
        return webClient.get().uri(String.format("/cdn/%s/data/ko_KR/item.json", version))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(root -> root.get("data"))
                .doOnNext(data -> {
                    // "data" 노드 아래의 모든 아이템을 맵에 저장합니다.
                    data.fields().forEachRemaining(entry -> {
                        itemIdToInfo.put(entry.getKey(), entry.getValue());
                    });
                })
                .then();
    }

    private Mono<Void> loadSpells(String version) {
        return webClient.get().uri(String.format("/cdn/%s/data/ko_KR/summoner.json", version))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(root -> root.get("data"))
                .doOnNext(data -> {
                    data.fields().forEachRemaining(entry -> {
                        // 스펠은 key가 숫자 ID입니다.
                        String id = entry.getValue().get("key").asText();
                        spellIdToInfo.put(id, entry.getValue());
                    });
                })
                .then();
    }

    // --- 🎯 새로 추가된 부분 (3/3): 룬 데이터 로드 및 파싱 메서드 ---

    /**
     * 룬 정보를 DDragon에서 가져와 캐시에 저장합니다.
     * /cdn/{version}/data/ko_KR/runesReforged.json 엔드포인트를 사용합니다.
     */
    private Mono<Void> loadRunes(String version) {
        String url = String.format("/cdn/%s/data/ko_KR/runesReforged.json", version);

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .timeout(TIMEOUT)
                .doOnNext(runePaths -> {
                    // 룬 경로(정밀, 지배 등) 배열을 순회하며 모든 하위 룬의 정보를 Map에 저장합니다.
                    runePaths.forEach(this::processRunePaths);
                })
                .then(); // Void 반환
    }

    /**
     * DDragon의 룬 경로 배열(JsonNode)을 순회하며 모든 룬 ID와 정보를 Map에 저장합니다.
     * DDragon의 룬 데이터는 (경로 > 슬롯 > 룬)의 3중 구조로 중첩되어 있어 평탄화가 필요합니다.
     * @param runePath 정밀, 지배 등 하나의 룬 경로 정보
     */
    private void processRunePaths(JsonNode runePath) {

        // 🎯 [추가된 핵심 코드] 룬 경로(트리)의 ID와 정보를 맵에 저장합니다.
        // 이 정보는 '보조 룬 트리'의 이름과 이미지를 조회할 때 사용됩니다.
        int styleId = runePath.get("id").asInt(); // 8000 (정밀), 8100 (지배) 등
        runeIdToInfo.put(styleId, runePath); // runePath에는 name 필드(예: "정밀")가 포함되어 있습니다.

        // 룬 경로는 slots 배열을 가집니다. (예: 정밀 룬의 첫 번째 슬롯, 두 번째 슬롯 등)
        if (runePath.has("slots")) {
            runePath.get("slots").forEach(slot -> {
                // 각 슬롯은 runes 배열을 가집니다. (예: 공격력 강화 룬 3가지)
                if (slot.has("runes")) {
                    slot.get("runes").forEach(rune -> {
                        // 룬 ID (int)와 해당 룬의 JsonNode를 맵에 저장합니다.
                        // 이 정보는 '핵심 룬'의 이름과 이미지를 조회할 때 사용됩니다.
                        int id = rune.get("id").asInt(); // 8005 (집중 공격) 등
                        runeIdToInfo.put(id, rune);
                    });
                }
            });
        }
    }

    // ------------------------------------------------------------------


    /* --- 공통 데이터 조회 메서드 (룬 관련 Getter 추가) --- */

    /**
     * 챔피언 ID(숫자)를 챔피언 이름(영문)으로 변환합니다.
     */
    public String getChampionNameById(long id) {
        return championIdToName.getOrDefault(String.valueOf(id), "Unknown Champion");
    }

    /**
     * 챔피언 이름(영문)을 기반으로 챔피언 이미지 URL을 생성합니다.
     */
    public String getChampionImageUrl(String championName) {
        return Optional.ofNullable(championNameToImage.get(championName))
                .map(imageNode -> String.format("https://ddragon.leagueoflegends.com/cdn/%s/img/champion/%s", latestVersion, imageNode.get("full").asText()))
                .orElse("placeholder_champion_url");
    }

    /**
     * 아이템 ID를 기반으로 아이템 이름(한글)을 조회합니다.
     */
    public String getItemNameById(int id) {
        if (id == 0) return "빈칸";
        // DDragon 데이터는 ko_KR로 로드되므로, "name" 필드에 한글 이름이 포함되어 있습니다.
        return Optional.ofNullable(itemIdToInfo.get(String.valueOf(id)))
                .map(itemNode -> itemNode.get("name").asText())
                .orElse("Unknown Item");
    }

    /**
     * 아이템 ID를 기반으로 아이템 이미지 URL을 생성합니다.
     */
    public String getItemImageUrl(int id) {
        if (id == 0) return "placeholder_empty_item"; // 아이템 ID 0인 경우
        return Optional.ofNullable(itemIdToInfo.get(String.valueOf(id)))
                .map(itemNode -> String.format("https://ddragon.leagueoflegends.com/cdn/%s/img/item/%s", latestVersion, itemNode.get("image").get("full").asText()))
                .orElse("placeholder_item_url");
    }

    /**
     * 소환사 주문(스펠) ID를 기반으로 스펠 이름(한글)을 조회합니다.
     */
    public String getSpellNameById(int id) {
        return Optional.ofNullable(spellIdToInfo.get(String.valueOf(id)))
                .map(spellNode -> spellNode.get("name").asText())
                .orElse("Unknown Spell");
    }

    /**
     * 소환사 주문(스펠) ID를 기반으로 스펠 이미지 URL을 생성합니다.
     */
    public String getSpellImageUrl(int id) {
        return Optional.ofNullable(spellIdToInfo.get(String.valueOf(id)))
                .map(spellNode -> String.format("https://ddragon.leagueoflegends.com/cdn/%s/img/spell/%s", latestVersion, spellNode.get("image").get("full").asText()))
                .orElse("placeholder_spell_url");
    }

    // --- 🎯 룬 관련 Getter 메서드 추가 ---
    /**
     * 룬 ID를 기반으로 룬 이름(한글)을 조회합니다.
     */
    public String getRuneNameById(int id) {
        return Optional.ofNullable(runeIdToInfo.get(id))
                .map(runeNode -> runeNode.get("name").asText())
                .orElse("Unknown Rune");
    }

    /**
     * 룬 ID를 기반으로 룬 이미지 URL을 생성합니다.
     */
    public String getRuneImageUrl(int id) {
        return Optional.ofNullable(runeIdToInfo.get(id))
                .map(runeNode -> String.format("https://ddragon.leagueoflegends.com/cdn/img/%s", runeNode.get("icon").asText()))
                .orElse("placeholder_rune_url");
    }
    /**
     * 프로필 아이콘 ID를 기반으로 아이콘 이미지 URL을 생성합니다.
     */
    public String getProfileIconUrl(int id) {
        if (latestVersion != null) {
            // DDragon 이미지 경로 형식: {base}/cdn/{version}/img/profileicon/{id}.png
            return String.format("https://ddragon.leagueoflegends.com/cdn/%s/img/profileicon/%d.png", latestVersion, id);
        }
        return "placeholder_icon_url";
    }
}