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
import com.example.oops.cofig.security.util.TokenInfo;
import com.example.oops.common.ApiResponseEntity;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final SignService signService;

    private final LoginService loginService;

    private final EmailService emailService;

    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;



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
    public ResponseEntity<Void> refreshToken(@RequestBody RefreshRequestDto refreshRequestDto, HttpServletResponse response) {
        loginService.refreshToken(refreshRequestDto,response);
       return ResponseEntity.ok().build();
    }


}
