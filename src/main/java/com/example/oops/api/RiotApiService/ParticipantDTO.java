package com.example.oops.api.RiotApiService;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class  ParticipantDTO {
    private String puuid;
    private boolean win;
    private int kills;
    private int deaths;
    private int assists;
    private String championName;
    private int item0; // 아이템 슬롯 0번
    private int item1;
    private int item2;
    private int item3;
    private int item4;
    private int item5;
    private int item6; // 장신구
    private int summoner1Id; // 스펠 1
    private int summoner2Id; // 스펠 2
    private int totalMinionsKilled; // CS
    private String riotIdGameName;
    private String riotIdTagline;
    private int totalDamageDealtToChampions;
    private int totalDamageTaken;
    private PerksDto perks;
    private int neutralMinionsKilled;
}