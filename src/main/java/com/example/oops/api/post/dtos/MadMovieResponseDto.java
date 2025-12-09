package com.example.oops.api.post.dtos;

import com.example.oops.api.comment.CommentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class MadMovieResponseDto {
    private Long id;
    private String title;
    private int upVote;
    private int downVote;
    private LocalDateTime createdOn;
    private String videoUrl;
    List<CommentResponseDto> comments;
    private Long UserId;
    private String UserName;
    private int viewCount;
    private Boolean isNotice;
    private String content;
}
