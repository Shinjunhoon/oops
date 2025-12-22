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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class DDragonService {

    private final WebClient webClient;
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    // 🔥 병렬 / 순차 비교 플래그
    private static final boolean USE_PARALLEL_LOADING = true;

    @Getter
    private String latestVersion;

    private final Map<String, String> championIdToName = new HashMap<>();
    private final Map<String, JsonNode> championNameToImage = new HashMap<>();
    private final Map<String, JsonNode> itemIdToInfo = new HashMap<>();
    private final Map<String, JsonNode> spellIdToInfo = new HashMap<>();
    private final Map<Integer, JsonNode> runeIdToInfo = new HashMap<>();

    public DDragonService(
            WebClient.Builder webClientBuilder,
            @Value("${ddragon.base-url}") String ddragonBaseUrl
    ) {
        final int MAX_BUFFER_SIZE = 5 * 1024 * 1024;

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(MAX_BUFFER_SIZE))
                .build();

        this.webClient = webClientBuilder
                .baseUrl(ddragonBaseUrl)
                .exchangeStrategies(strategies)
                .build();
    }

    /* =========================
       🔹 Application Startup
       ========================= */

    @PostConstruct
    public void init() {
        log.info("DDragon 데이터 로드 시작...");
        long start = System.currentTimeMillis();

        try {
            latestVersion = getLatestVersion().block(TIMEOUT);

            if (latestVersion == null) {
                throw new IllegalStateException("DDragon version fetch failed");
            }

            if (USE_PARALLEL_LOADING) {
                loadAllStaticDataParallel(latestVersion).block(TIMEOUT);
            } else {
                loadAllStaticDataSequential(latestVersion).block(TIMEOUT);
            }

            long end = System.currentTimeMillis();

            log.info(
                    "[{}] DDragon Load Complete | Time: {} ms | Champions: {}, Items: {}, Spells: {}, Runes: {}",
                    USE_PARALLEL_LOADING ? "PARALLEL" : "SEQUENTIAL",
                    end - start,
                    championIdToName.size(),
                    itemIdToInfo.size(),
                    spellIdToInfo.size(),
                    runeIdToInfo.size()
            );

        } catch (Exception e) {
            log.error("DDragon 초기화 실패", e);
        }
    }

    /* =========================
       🔹 Parallel / Sequential
       ========================= */

    private Mono<Void> loadAllStaticDataParallel(String version) {
        return Mono.when(
                loadChampions(version),
                loadItems(version),
                loadSpells(version),
                loadRunes(version)
        );
    }

    private Mono<Void> loadAllStaticDataSequential(String version) {
        return loadChampions(version)
                .then(loadItems(version))
                .then(loadSpells(version))
                .then(loadRunes(version));
    }

    /* =========================
       🔹 API Load Methods
       ========================= */

    private Mono<String> getLatestVersion() {
        return webClient.get()
                .uri("/api/versions.json")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get(0).asText());
    }

    private Mono<Void> loadChampions(String version) {
        return webClient.get()
                .uri("/cdn/{v}/data/ko_KR/champion.json", version)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(root -> root.get("data"))
                .doOnNext(data ->
                        data.fields().forEachRemaining(e -> {
                            String name = e.getKey();
                            String id = e.getValue().get("key").asText();
                            championIdToName.put(id, name);
                            championNameToImage.put(name, e.getValue().get("image"));
                        })
                )
                .then();
    }

    private Mono<Void> loadItems(String version) {
        return webClient.get()
                .uri("/cdn/{v}/data/ko_KR/item.json", version)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(root -> root.get("data"))
                .doOnNext(data ->
                        data.fields().forEachRemaining(e ->
                                itemIdToInfo.put(e.getKey(), e.getValue()))
                )
                .then();
    }

    private Mono<Void> loadSpells(String version) {
        return webClient.get()
                .uri("/cdn/{v}/data/ko_KR/summoner.json", version)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(root -> root.get("data"))
                .doOnNext(data ->
                        data.fields().forEachRemaining(e -> {
                            String id = e.getValue().get("key").asText();
                            spellIdToInfo.put(id, e.getValue());
                        })
                )
                .then();
    }

    private Mono<Void> loadRunes(String version) {
        return webClient.get()
                .uri("/cdn/{v}/data/ko_KR/runesReforged.json", version)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .timeout(TIMEOUT)
                .doOnNext(paths -> paths.forEach(this::processRunePaths))
                .then();
    }

    private void processRunePaths(JsonNode runePath) {
        int styleId = runePath.get("id").asInt();
        runeIdToInfo.put(styleId, runePath);

        runePath.get("slots").forEach(slot ->
                slot.get("runes").forEach(rune ->
                        runeIdToInfo.put(rune.get("id").asInt(), rune))
        );
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