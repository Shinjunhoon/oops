package com.example.oops.api.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DiscussionListResponseDto {
    // 제목,내용,생성일자추천수, 반대수

    private Long id;
    private String title;
    private int upVoteCount;
    private int downVoteCount;
    private LocalDateTime createdAt;
}
