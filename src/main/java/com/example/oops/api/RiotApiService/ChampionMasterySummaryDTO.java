package com.example.oops.api.RiotApiService;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChampionMasterySummaryDTO {
    private String championName;         // DDragon 적용 (한글)
    private String championImageUrl;     // DDragon 적용
    private int championLevel;
    private long championPoints;
}
