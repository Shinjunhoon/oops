package com.example.oops.api.report;


import com.example.oops.cofig.security.provider.JwtTokenProvider;
import com.example.oops.common.ApiResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports") // 복수형으로 통일하는 것이 RESTful 관례에 적합
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final JwtTokenProvider jwtTokenProvider;
    @PostMapping("/posts")
    public ResponseEntity<ApiResponseEntity> report(@RequestBody ReportRequest reportRequest, Authentication authentication) {

        return ApiResponseEntity.successResponseEntity(reportService.report(reportRequest,jwtTokenProvider.getLoginId(authentication)));
    }

    @PostMapping("/comments")
    public ResponseEntity<ApiResponseEntity> user(@RequestBody UserReportRequestDto userReportRequestDto, Authentication authentication) {

        return ApiResponseEntity.successResponseEntity(reportService.userReport(userReportRequestDto,jwtTokenProvider.getLoginId(authentication)));
    }


}
