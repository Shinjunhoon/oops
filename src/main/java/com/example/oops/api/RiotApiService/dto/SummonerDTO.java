package com.example.oops.api.RiotApiService.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SummonerDTO {
    private String puuid;
    private int profileIconId;
    private long summonerLevel;
    private long revisionDate;
}