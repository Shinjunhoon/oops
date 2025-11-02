package com.example.oops.api.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommentUpdateRequestDto {
    private Long commentId;
    private String content;
}
