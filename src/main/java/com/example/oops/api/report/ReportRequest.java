package com.example.oops.api.report;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportRequest {
    private Long postId;
    private String reportType;
    private String reason;
}
