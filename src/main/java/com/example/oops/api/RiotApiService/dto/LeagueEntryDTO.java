package com.example.oops.api.RiotApiService.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LeagueEntryDTO {
    private String queueType; // RANKED_SOLO_5x5, RANKED_FLEX_SR
    private String tier;
    private String rank; // I, II, III, IV
    private int leaguePoints;
    private int wins;
    private int losses;
}
