package com.example.oops.api.post.dtos;

import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.post.domain.enums.GameCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MadMovieRequestDto {
    private String title;
    private String videoUrl;
    private BoardType boardType;
    private String content;
    private GameCategory gameCategory;
}
