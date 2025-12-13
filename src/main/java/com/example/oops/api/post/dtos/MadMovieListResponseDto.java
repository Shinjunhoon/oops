package com.example.oops.api.post.dtos;

import com.example.oops.api.comment.CommentResponseDto;
import com.example.oops.api.post.domain.Post;
import com.example.oops.api.post.domain.enums.GameCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class MadMovieListResponseDto {

    private Long id;
    private String title;
    private String gameCategory;
    private String content;
    private int upVote;
    private int downVote;
    private LocalDateTime createdOn;
    private int viewCount;
    private String userName;
    private boolean isNotice;
    private String videoUrl;
    private Long userId;
    List<CommentResponseDto> comments;

    public MadMovieListResponseDto(Long id, String title, GameCategory gameCategory, String content,
                                   int upVote, int downVote, LocalDateTime createdOn, int viewCount,
                                   String userName, Boolean isNotice, String videoUrl,Long userId) {
        this.id = id;
        this.title = title;
        this.gameCategory = gameCategory.getMessage(); // 한국어 메시지 사용
        this.content = content;
        this.upVote = upVote;
        this.downVote = downVote;
        this.createdOn = createdOn;
        this.viewCount = viewCount;
        this.userName = userName;
        this.isNotice = isNotice;
        this.videoUrl = videoUrl;
        this.userId = userId;
    }
}
