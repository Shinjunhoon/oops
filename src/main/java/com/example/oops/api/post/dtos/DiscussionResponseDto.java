package com.example.oops.api.post.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class DiscussionResponseDto {
    private Long id;
    private String title;
    private String content;
    private String argument1;
    private String argument2;
    private int upVoteCount;
    private int downVoteCount;
    private LocalDateTime createdAt;
}
