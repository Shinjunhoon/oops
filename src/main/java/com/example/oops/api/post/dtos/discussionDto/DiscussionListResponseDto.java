package com.example.oops.api.post.dtos.discussionDto;

import com.example.oops.api.post.domain.enums.Champion;
import com.example.oops.api.post.domain.enums.Tier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@NoArgsConstructor
@Getter
public class DiscussionListResponseDto {
    // 제목,내용,생성일자추천수, 반대수

    private Long id;
    private String title;
    private int upVoteCount;
    private int downVoteCount;
    private LocalDateTime createdAt;
    private Champion champion1;
    private Champion champion2;
    private Tier tier;
    private String koreanName1;
    private String koreanName2;

    public DiscussionListResponseDto(
            Long id,
            String title,
            int upVoteCount,
            int downVoteCount,
            LocalDateTime createdAt,
            Champion champion1,
            Champion champion2,
            Tier tier)
    {
        this.id = id;
        this.title = title;
        this.upVoteCount = upVoteCount;
        this.downVoteCount = downVoteCount;
        this.createdAt = createdAt;
        this.champion1 = champion1;
        this.champion2 = champion2;
        this.tier = tier;

        // koreanName1, koreanName2는 JPQL에서 채우지 않으므로 여기서 설정하지 않습니다.
    }
}
