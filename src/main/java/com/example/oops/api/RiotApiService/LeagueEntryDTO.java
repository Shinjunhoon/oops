package com.example.oops.api.RiotApiService;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LeagueEntryDTO {
    private String queueType;     // 랭크 유형 (예: RANKED_SOLO_5x5)
    private String tier;          // 티어 (예: PLATINUM)
    private String rank;          // 등급 (예: III)
    private int leaguePoints;     // LP

    // 상세 정보 (전적 검색 사이트에서 흔히 사용)
    private int wins;             // 승리 횟수
    private int losses;           // 패배 횟수
}
