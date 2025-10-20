package com.example.oops.api.user.controller;


import com.example.oops.api.email.dto.EmailSendRequestDto;
import com.example.oops.api.email.dto.EmailVerifyRequest;
import com.example.oops.api.email.service.EmailService;
import com.example.oops.api.user.application.LoginService;
import com.example.oops.api.user.application.SignService;
import com.example.oops.api.user.dto.LoginRequestDto;
import com.example.oops.api.user.dto.SignRequestDto;
import com.example.oops.api.user.repository.RefreshRequestDto;
import com.example.oops.cofig.security.util.TokenInfo;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final SignService signService;
    private final LoginService loginService;

    private final EmailService emailService;



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
    public ResponseEntity<TokenInfo> refreshToken(@RequestBody RefreshRequestDto refreshRequestDto) {
       return ResponseEntity.ok(loginService.refreshToken(refreshRequestDto));
    }


}
