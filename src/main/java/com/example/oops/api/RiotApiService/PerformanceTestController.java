package com.example.oops.api.RiotApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/perf")
public class PerformanceTestController {

    private final RiotApiService riotApiService;
    private final DDragonService dDragonService;

    /**
     * 🔴 Riot API 실시간 호출 → ID → 이름 변환
     */
    @GetMapping("/riot/champion/{puuid}")
    public String riotApiTest(@PathVariable String puuid) {
        long start = System.currentTimeMillis();

        int championId = riotApiService
                .getChampionIdByPuuid(puuid)
                .block();

        String name = dDragonService.getChampionNameById(championId);

        long end = System.currentTimeMillis();

        log.info("[RIOT API] ChampionId={} | Time={} ms",
                championId, end - start);

        return name;
    };

    /**
     * 🟢 메모리 캐시 조회
     */
    @GetMapping("/cache/champion/{id}")
    public String cacheTest(@PathVariable long id) {
        long start = System.currentTimeMillis();

        String name = dDragonService.getChampionNameById(id);

        long end = System.currentTimeMillis();

        log.info("[CACHE] ChampionId={} | Time={} ms", id, end - start);

        return name;
    }
}
