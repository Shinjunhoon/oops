package com.example.oops.api.RiotApiService.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChampionMasteryDTO {
    private long championId;
    private int championLevel;
    private int championPoints;
    private long lastPlayTime;
    private int tokensEarned;
}
