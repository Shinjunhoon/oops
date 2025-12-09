package com.example.oops.api.report;

import com.example.oops.api.comment.Comment;
import com.example.oops.api.comment.CommentRepository;
import com.example.oops.api.post.domain.Post;
import com.example.oops.api.post.repository.PostRepository;
import com.example.oops.api.user.domain.User;
import com.example.oops.api.user.repository.UserRepository;
import com.example.oops.common.error.ErrorCode;
import com.example.oops.common.error.OopsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final PostRepository postRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;


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
                .reportTypes(ReportTypes.POST)
                .postId(report.getPostId())
                .boardType(post.getBoardType())
                .reportType(ReportType.valueOf(report.getReportType()))
                .reasonDetail(report.getReason())
                .reporterUserId(userId)
                .build();


        reportRepository.save(report1);

        return "신고 성공";

    }


    public String userReport(UserReportRequestDto userReportRequestDto, Long userId) {

        Post post = postRepository.findById(userReportRequestDto.getPostId()).orElseThrow(() -> new OopsException(ErrorCode.COMMENT_NOT_FOUND));
        // 신고자 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new OopsException(ErrorCode.USER_NOT_FOUND));

        // 신고된 댓글 찾기
        Comment comment = commentRepository.findById(userReportRequestDto.getCommentId())
                .orElseThrow(() -> new OopsException(ErrorCode.COMMENT_NOT_FOUND));

        // 댓글 작성자 (신고 대상)
        User reportedUser = userRepository.findById(comment.getUser().getId())
                .orElseThrow(() -> new OopsException(ErrorCode.USER_NOT_FOUND));

        Report report = Report.builder()
                .reportTypes(ReportTypes.COMMENT)
                .commentId(comment.getId())
                .reportedId(reportedUser.getId())
                .reportType(ReportType.valueOf(userReportRequestDto.getReportType()))
                .reasonDetail(userReportRequestDto.getReason())
                .reporterUserId(userId)
                .reporterName(user.getUsername())
                .reportedName(reportedUser.getUsername())
                .comment(comment.getComment())
                .postId(userReportRequestDto.getPostId())
                .boardType(post.getBoardType())
                .build();

        reportRepository.save(report);

        return "신고 성공";
}
    }


