package com.example.oops.api.RiotApiService;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChampionMasteryDTO {
    private String puuid;
    private int championId;          // 💡 챔피언 ID (이후 이름으로 변환 필요)
    private int championLevel;       // 마스터리 레벨
    private int championPoints;      // 마스터리 점수
    private long lastPlayTime;       // 마지막 플레이 시간 (epoch millis)

    // 다음 레벨 관련 정보
    private int championPointsSinceLastLevel;
    private int championPointsUntilNextLevel;

    // 토큰/등급 관련 정보
    private int tokensEarned;
}
