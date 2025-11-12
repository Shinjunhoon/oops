package com.example.oops.api.post.dtos.discussionDto;

import com.example.oops.api.post.domain.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class MyPostResponse {

    private Long postId;
    private String title;
    private LocalDateTime createdAt;

    public MyPostResponse(Long postId, String title, LocalDateTime createdAt) {
        this.postId = postId;
        this.title = title;
        this.createdAt = createdAt;
    }

    public static MyPostResponse of(Post post) {
        return new MyPostResponse(
                post.getId(),
                post.getTitle(),
                post.getCreatedAt()
        );
    }
}
