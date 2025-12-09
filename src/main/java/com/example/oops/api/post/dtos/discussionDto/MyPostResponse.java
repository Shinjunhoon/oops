package com.example.oops.api.post.dtos.discussionDto;

import com.example.oops.api.post.domain.Post;
import com.example.oops.api.post.domain.enums.BoardType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class MyPostResponse {

    private Long postId;
    private String title;
    private LocalDateTime createdAt;
    private BoardType boardType;

    public MyPostResponse(Long postId, String title, LocalDateTime createdAt, BoardType boardType) {
        this.postId = postId;
        this.title = title;
        this.createdAt = createdAt;
        this.boardType = boardType;
    }

    public static MyPostResponse of(Post post) {
        return new MyPostResponse(
                post.getId(),
                post.getTitle(),
                post.getCreatedAt(),
                post.getBoardType()
        );
    }
}
