package com.example.oops.api.RiotApiService;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SummonerDTO {

    private String puuid;
    private int profileIconId;
    private long revisionDate;
    private long summonerLevel;
    private String name;
    private String tag;

    // 💡 3단계 랭크 정보 필드 (LeagueEntryDTO 타입 사용)
    private LeagueEntryDTO soloRank;  // 솔로 랭크 정보
    private LeagueEntryDTO flexRank;  // 자유 랭크 정보
    }