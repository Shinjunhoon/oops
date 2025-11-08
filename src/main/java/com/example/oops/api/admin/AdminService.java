package com.example.oops.api.admin;

import com.example.oops.api.post.application.discussionimpl.PostDelService;
import com.example.oops.api.post.repository.PostRepository;
import com.example.oops.api.report.Report;
import com.example.oops.api.report.ReportRepository;
import com.example.oops.api.report.ReportResponseDto;
import com.example.oops.api.report.ReportService;
import com.example.oops.api.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final PostDelService postDelService;

    /**
     * 데이터베이스에 저장된 총 사용자 수를 조회합니다.
     */
    public long getTotalUserCount() {
        return userRepository.count();
    }


    public List<ReportResponseDto> getReports() {
       return reportRepository.findAllReportsAsDto();
    }

    @Transactional
    public String deletePostAndProcessReports(Long postId, Long adminUserId) {


        postDelService.deletePost(postId, adminUserId);

        reportRepository.updateProcessedStatusByPostId(postId);

        return "success";
    }
}
