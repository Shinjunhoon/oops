package com.example.oops.api.RiotApiService;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SummonerProfileDTO {
    private SummonerDTO profile;
    private List<ChampionMasteryDTO> masteries;
    private List<MatchDetailDTO> matchHistory;
}