package com.example.oops.api.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommentCreateRequestDto {

    @NotBlank(message = "댓긇 내용은 필수입니다.")
    @Size(max = 500, message = "댓글 내용은 500자 이하로 입력해주세요")
    private String content;

    private Long postId;
}
