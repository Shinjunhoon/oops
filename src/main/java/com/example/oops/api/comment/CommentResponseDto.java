package com.example.oops.api.comment;

import com.example.oops.api.user.domain.enums.Line;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CommentResponseDto {
    private String comment;
    private Long commentId;
    private String username;
    private Line line;
    private Long userId;

    public static CommentResponseDto of(Comment comment) {
        return new CommentResponseDto(
                comment.getComment(),
                comment.getId(),
                comment.getUserName(),
                comment.getLine(),
                comment.getUser().getId()
        );
    }
}
