package com.example.oops.api.report;

import com.example.oops.api.post.domain.enums.BoardType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserReportResponseDto {
    /* 필요헌 팔드
    리포터 이름
    리포터 디테일
    신고자 이름
    신고 당한 이름
    신고자 ID
    신고 당한 ID
    신고 댓글 확인
    * */
    private String reportName;
    private String reasonDetail;
    private String reporterName;   // 신고자 이름
    private String reportedName;
    private Long reporterId;   // 신고자 ID
    private Long reportedId;   // 신고 대상자 ID
    private String comment;
    private Boolean processed;
    private Long postId;
    private Long commentId;
    private BoardType boardType;


    public UserReportResponseDto(ReportType reportType, String reasonDetail,
                     String reporterName, String reportedName,
                     Long reporterId, Long reportedId,
                     String comment, Boolean processed, Long postId, Long commentId, BoardType boardType) {

        this.reportName = reportType.getDescription();
        this.reasonDetail = reasonDetail;
        this.reporterName = reporterName;
        this.reportedName = reportedName;
        this.reporterId = reporterId;
        this.reportedId = reportedId;
        this.comment = comment;
        this.processed = processed;
        this.postId = postId;
        this.commentId = commentId;
        this.boardType = boardType;
    }


}
