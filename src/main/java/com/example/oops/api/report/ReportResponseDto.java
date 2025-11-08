package com.example.oops.api.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@Getter

public class ReportResponseDto {
    private String reportName;
    private String reasonDetail;
    private Long postId;
    private Long userId;
    private Boolean processed;

    public ReportResponseDto(ReportType reportType, String reasonDetail, Long postId, Long userId,Boolean processed) {
        this.reportName =  reportType.getDescription();
        this.reasonDetail = reasonDetail;
        this.postId = postId;
        this.userId = userId;
        this.processed = processed;
    }
}
