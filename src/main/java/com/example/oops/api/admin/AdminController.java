package com.example.oops.api.admin;

import com.example.oops.cofig.security.provider.JwtTokenProvider;
import com.example.oops.common.ApiResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;

@RestController
@RequestMapping("/api/admin") // 관리자 API 경로는 보통 별도로 분리합니다.
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final JwtTokenProvider jwtTokenProvider;

    // 응답 형태를 표준화하기 위한 클래스 (예: ApiResponse)를 사용한다고 가정합니다.

    @GetMapping("/users/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getUserCount() {
        long count = adminService.getTotalUserCount();

        // 예시: 간단히 Long 타입 숫자를 반환합니다.
        return ResponseEntity.ok(count);
    }

    @GetMapping("/getReport")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseEntity> getReport(){
        return ApiResponseEntity.successResponseEntity(adminService.getReports());
    }

    @DeleteMapping("/posts/{postId}") // HTTP DELETE를 사용하여 리소스 삭제를 명확히 함
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseEntity> deletePostByAdmin(@PathVariable Long postId, Authentication authentication) {


        return ApiResponseEntity.successResponseEntity(adminService.deletePostAndProcessReports(postId, jwtTokenProvider.getLoginId(authentication)));
    }

}
