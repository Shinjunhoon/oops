package com.example.oops.api.RiotApiService.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MatchDetailDTO {
    private MatchInfoDto info;

    @Data
    @NoArgsConstructor
    public class MatchInfoDto { // 'static' 제거됨
        private long gameCreation;
        private long gameDuration;
        private int queueId;
        private List<ParticipantDTO> participants;
    }

    @Data
    @NoArgsConstructor
    public class ParticipantDTO { // 'static' 제거됨
        private String puuid;
        private boolean win;
        private int kills;
        private int deaths;
        private int assists;
        // 챔피언 이름은 이미 영문으로 제공되므로 변환이 필요 없습니다.
        private String championName;
        private int item0;
        private int item1;
        private int item2;
        private int item3;
        private int item4;
        private int item5;
        private int item6; // 와드 아이템
        private int summoner1Id;
        private int summoner2Id;
        private int totalMinionsKilled;
        private String riotIdGameName;
        private String riotIdTagline;
    }
}