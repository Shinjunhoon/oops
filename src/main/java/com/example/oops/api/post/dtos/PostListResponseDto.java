package com.example.oops.api.post.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class PostListResponseDto {
    private Long id;
    private String title;
    private LocalDateTime createdOn;
    private String userName;
    private int viewCount;
    private  boolean isNotice;
}
