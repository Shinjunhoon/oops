package com.example.oops.api.post.dto;

import com.example.oops.api.post.domain.enums.BoardType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PostRequestDto {

    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private BoardType boardType;
}
