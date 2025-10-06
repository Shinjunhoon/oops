package com.example.oops.api.post.dtos.badReportDto;

import com.example.oops.api.post.domain.enums.BoardType;
import com.example.oops.api.user.domain.enums.Line;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BadReportRequestDto {
    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private BoardType boardType;

    @NotNull
    private Line line1;

    @NotNull
    private Line line2;

}
