package com.example.oops.api.post.domain.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum GameCategory {
    ALL("전체"),LOL("리그오브레전드"),VALORANT("발로란트");

    private final String message;
}
