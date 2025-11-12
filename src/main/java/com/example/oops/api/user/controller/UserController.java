package com.example.oops.api.user.controller;


import com.example.oops.api.email.dto.EmailSendRequestDto;
import com.example.oops.api.email.dto.EmailVerifyRequest;
import com.example.oops.api.email.service.EmailService;
import com.example.oops.api.user.application.LoginService;
import com.example.oops.api.user.application.SignService;
import com.example.oops.api.user.application.UserService;
import com.example.oops.api.user.dto.LoginRequestDto;
import com.example.oops.api.user.dto.SignRequestDto;
import com.example.oops.api.user.dto.UserIdCheckRequestDto;
import com.example.oops.api.user.repository.RefreshRequestDto;
import com.example.oops.cofig.security.provider.JwtTokenProvider;
import com.example.oops.cofig.security.util.CookieUtil;
import com.example.oops.cofig.security.util.TokenInfo;
import com.example.oops.common.ApiResponseEntity;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.example.oops.cofig.security.util.CookieUtil.REFRESH_TOKEN_COOKIE_NAME;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final SignService signService;

    private final LoginService loginService;

    private final EmailService emailService;

    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;

    private final CookieUtil cookieUtil;


    @PostMapping("/sign")
    public ResponseEntity<String> sign(@RequestBody @Valid SignRequestDto signRequestDto) {
        signService.Sign(signRequestDto);
        return ResponseEntity.ok().body("Success");
    }


    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        loginService.login(loginRequestDto,response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/checkUserName")
    public ResponseEntity<ApiResponseEntity> checkUserName(@RequestBody @Valid UserIdCheckRequestDto userIdCheckRequestDto) {
        return ApiResponseEntity.successResponseEntity(userService.checkUserId(userIdCheckRequestDto));
    }

    @GetMapping("/getUser")
    public ResponseEntity<ApiResponseEntity> getUser(Authentication authentication) {
        return ApiResponseEntity.successResponseEntity(userService.getUserInfo(jwtTokenProvider.getLoginId(authentication)));
    }

    @PostMapping("/email")
    public ResponseEntity<String> senderAuthCode(@Valid @RequestBody EmailSendRequestDto emailSendRequestDto, HttpServletResponse response) throws MessagingException {
        emailService.sendEmail(emailSendRequestDto.getEmail());
        return ResponseEntity.ok().body("Success");

    }

    @PostMapping("/emailVerify")
    public ResponseEntity<String> senderAuthCode(@Valid @RequestBody EmailVerifyRequest emailVerifyRequest, HttpServletResponse response) throws MessagingException {
        emailService.verifyCode(emailVerifyRequest.getAuthCode(),emailVerifyRequest.getEmail());
        return ResponseEntity.ok().body("Success");

    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(@CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = true) String refreshToken, HttpServletResponse response) {
        loginService.refreshToken(refreshToken,response);
       return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            @CookieValue(value = CookieUtil.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response // 쿠키 삭제를 위해 응답 객체 사용
    ) {

        String accessToken = jwtTokenProvider.resolveToken(request);
        System.out.println("[Logout] AccessToken: " + accessToken);
        System.out.println("[Logout] refreshToken: " + refreshToken);
        // 1. 토큰 유효성 검사 및 서버 로직 호출
        if (accessToken != null && refreshToken != null){

            String userName = jwtTokenProvider.getUserPk(accessToken);

            // 2. Service 계층에 로그아웃 비즈니스 로직 위임
            loginService.logout(accessToken, userName);
        }

        cookieUtil.deleteRefreshTokenCookie(response);

        // 클라이언트에게 성공 응답 반환
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delUser")
    public ResponseEntity<ApiResponseEntity> delUser(Authentication authentication) {

        return  ApiResponseEntity.successResponseEntity(userService.deleteUser(jwtTokenProvider.getLoginId(authentication)));
    }
}
