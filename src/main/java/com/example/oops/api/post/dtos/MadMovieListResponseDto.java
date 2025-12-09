package com.example.oops.api.post.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class MadMovieListResponseDto {

    private Long id;
    private String title;
    private int upVote;
    private int downVote;
    private LocalDateTime createdOn;
    private int viewCount;
    private String userName;
    private  boolean isNotice;
}
