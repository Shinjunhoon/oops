package com.example.oops.api.post.dtos.badReportDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BadReportResponseDto {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
