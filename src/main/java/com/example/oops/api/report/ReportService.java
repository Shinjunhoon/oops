package com.example.oops.api.report;

import com.example.oops.api.post.domain.Post;
import com.example.oops.api.post.repository.PostRepository;
import com.example.oops.api.user.repository.UserRepository;
import com.example.oops.common.error.ErrorCode;
import com.example.oops.common.error.OopsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final PostRepository postRepository;
    private final ReportRepository reportRepository;

    public String report(ReportRequest report, Long userId) {

        Post post = postRepository.findById(report.getPostId()).orElseThrow(() -> new OopsException(ErrorCode.POST_NOT_FOUND));


        if (post.getUser().getId().equals(userId)) {
            throw new OopsException(ErrorCode.REPORT_OWN_POST_FORBIDDEN);
        }

        if (reportRepository.existsByPostIdAndReporterUserId(report.getPostId(), userId)) {
            throw new OopsException(ErrorCode.DUPLICATE_REPORT);
        }

        if (report.getReason() == null || report.getReason().trim().length() < 10) {
            throw new OopsException(ErrorCode.REPORT_REASON_TOO_SHORT);
        }

        ReportType type;
        try {
            type = ReportType.valueOf(report.getReportType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OopsException(ErrorCode.INVALID_REPORT_TYPE);
        }

        Report report1 = Report.builder()
                .postId(report.getPostId())
                .reportType(ReportType.valueOf(report.getReportType()))
                .reasonDetail(report.getReason())
                .reporterUserId(userId)
                .build();


        reportRepository.save(report1);

        return "신고 성공";

    }
}


