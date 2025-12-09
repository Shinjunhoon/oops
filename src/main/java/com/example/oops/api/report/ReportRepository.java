package com.example.oops.api.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {


    boolean existsByPostIdAndReporterUserId(Long postId, Long reporterUserId);

    @Query("SELECT new com.example.oops.api.report.ReportResponseDto(r.reportType,r.reasonDetail, r.postId, r.reporterUserId,r.processed,r.boardType) "
    +"from Report r " + "WHERE r.reportTypes = 'POST'")
    List<ReportResponseDto> findAllReportsAsDto();

    @Query("SELECT new com.example.oops.api.report.UserReportResponseDto("
            + "r.reportType, r.reasonDetail, "
            + "r.reporterName, r.reportedName, "
            + "r.reporterUserId, r.reportedId, "
            + "r.comment, r.processed, r.postId,r.commentId,r.boardType) "
            + "FROM Report r "
            + "WHERE r.commentId IS NOT NULL")
    List<UserReportResponseDto> findAllCommentReportsAsDto();




    @Modifying // 데이터 변경을 알리는 어노테이션
    @Query("UPDATE Report r SET r.processed = true WHERE r.postId = :postId AND r.processed = false")
    int updateProcessedStatusByPostId(@Param("postId") Long postId);
}
