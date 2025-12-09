package com.example.oops.api.RiotApiService.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PlayerDetailsDTO {
    // 프로필 정보
    private String gameName;
    private String tagLine;
    private String puuid;
    private int profileIconId;
    private String profileIconUrl; // 💡 DDragonService 적용
    private long summonerLevel;
    private String lastRevisionDateKr; // 💡 DDragonService 적용 (Timestamp 변환)

    // 랭크 정보 (솔로/자유)
    private LeagueRankInfo soloRank;
    private LeagueRankInfo flexRank;

    // 챔피언 숙련도
    private List<MasteryInfo> masteries;

    // 매치 기록 (가장 최근 1개)
    private List<MatchInfo> recentMatches;

    @Builder
    @Getter
    public static class LeagueRankInfo {
        private String queueType;
        private String tier;
        private String rank;
        private int leaguePoints;
        private int wins;
        private int losses;
        private double winRate;
    }

    @Builder
    @Getter
    public static class MasteryInfo {
        private String championNameKr; // 💡 DDragonService 적용
        private String championImageUrl; // 💡 DDragonService 적용
        private int level;
        private int points;
        private String lastPlayTimeKr; // 💡 DDragonService 적용 (Timestamp 변환)
    }

    @Builder
    @Getter
    public static class MatchInfo {
        private String matchId;
        private long gameDurationSeconds;
        private String gameCreationTimeKr; // 💡 DDragonService 적용 (Timestamp 변환)
        private String queueTypeKr; // 💡 DDragonService 적용 (Queue ID 변환)
        private List<ParticipantInfo> participants;
    }

    // 💡 새로운 룬 정보 내부 클래스: 주 룬/보조 룬 트리의 ID와 이름, 이미지 URL을 저장합니다.
    @Builder
    @Getter
    public static class RuneTreeInfo {
        private int styleId;
        private String name; // 룬 트리 이름 (예: "Precision", "지배")
        private String imageUrl; // 💡 DDragonService 적용 (룬 트리의 아이콘 이미지 URL)
    }


    @Builder
    @Getter
    public static class ParticipantInfo {
        private String riotIdGameName;
        private String riotIdTagline;
        private boolean win;
        private int kills;
        private int deaths;
        private int assists;
        private String kda;
        private String championName;
        private int totalDamageDealtToChampions;
        private int totalDamageTaken;
        private int cs;
        private String championImageUrl; // 💡 DDragonService 적용
        private List<ItemInfo> items;
        private SpellInfo spell1;
        private SpellInfo spell2;
        // 💡 룬 정보 추가: 주 룬 트리와 보조 룬 트리
        private RuneTreeInfo mainRuneTree;
        private RuneTreeInfo subRuneTree;
    }

    @Builder
    @Getter
    public static class ItemInfo {
        private String nameKr; // 💡 DDragonService 적용
        private String imageUrl; // 💡 DDragonService 적용
        private boolean isEmpty;
    }

    @Builder
    @Getter
    public static class SpellInfo {
        private int id;
        private String nameKr; // 💡 DDragonService 적용
        private String imageUrl; // 💡 DDragonService 적용
    }
}