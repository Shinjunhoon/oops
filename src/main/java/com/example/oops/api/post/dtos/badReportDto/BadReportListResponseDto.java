package com.example.oops.api.post.dtos.badReportDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BadReportListResponseDto {

    private Long id;
    private String title;
    private LocalDateTime createdAt;
}
