package com.example.oops.api.RiotApiService;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchDetailDTO {

    private MetadataDTO metadata;
    private InfoDTO info;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MetadataDTO {
        private String matchId;
    }
    // 💡 내부 클래스: 실제 필요한 매치 정보 (info 필드 아래에 위치)
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InfoDTO {
        private long gameCreation;
        private long gameDuration;
        private String gameMode;
        private String queueId;
        private List<ParticipantDTO> participants;
    }
}