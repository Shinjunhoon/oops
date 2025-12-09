package com.example.oops.api.admin;

import com.example.oops.api.comment.CommentRepository;
import com.example.oops.api.post.application.discussionimpl.PostDelService;
import com.example.oops.api.post.domain.Post;
import com.example.oops.api.post.repository.PostRepository;
import com.example.oops.api.report.*;
import com.example.oops.api.user.domain.User;
import com.example.oops.api.user.dto.UserListResponseDto;
import com.example.oops.api.user.repository.UserRepository;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final PostDelService postDelService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

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


    public List<UserReportResponseDto> getCommentReports() {


        return reportRepository.findAllCommentReportsAsDto();
    }

    public List<UserListResponseDto> getUsers() {

        return userRepository.findAllUserAsDto();

    }

    public UserListResponseDto getUserById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        UserListResponseDto dto = new UserListResponseDto(user.getUserInfo().getNickname(),user.getUserInfo().getEmail(),userId);

        return dto;

    }

    @Transactional
    public String  delUser(Long userId) {

        userRepository.deleteById(userId);


        return "회원 삭제 완료";

    }


    @Transactional
    public String  delComment(Long userId) {

        commentRepository.deleteById(userId);


        return "댓글 삭제 완료";

    }
}
