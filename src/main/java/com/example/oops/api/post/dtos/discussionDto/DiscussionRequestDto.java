package com.example.oops.api.post.dtos.discussionDto;

import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.user.domain.enums.Line;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DiscussionRequestDto {

    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private BoardType boardType;

    @NotNull
    private String argument1;

    @NotNull
    private String argument2;

    @NotNull
    private Line line1;

    @NotNull
    private Line line2;

}
