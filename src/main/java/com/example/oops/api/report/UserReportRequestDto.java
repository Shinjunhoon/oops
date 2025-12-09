package com.example.oops.api.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@Getter
public class UserReportRequestDto {
    private Long postId;
    private Long commentId;
    private String reportType;
    private String reason;
}
