package com.example.oops.api.post.dtos.DesPostListTopFive;

import com.example.oops.api.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostFiveResponseDto {
    private String title;
    private Long postId;
    private LocalDateTime createdOn;
    private String userName;
    private int viewCount;
    private Long commentCount;

    public PostFiveResponseDto(String title, Long postId, LocalDateTime createdOn, String userName, int viewCount, Long commentCount) {
        this.title = title;
        this.postId = postId;
        this.createdOn = createdOn;
        this.userName = userName;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
    }
}
