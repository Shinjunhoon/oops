package com.example.oops.api.user.domain.controller;


import com.example.oops.api.user.domain.application.LoginService;
import com.example.oops.api.user.domain.application.SignService;
import com.example.oops.api.user.domain.dto.LoginRequestDto;
import com.example.oops.api.user.domain.dto.SignRequestDto;
import com.example.oops.api.user.domain.repository.RefreshRequestDto;
import com.example.oops.api.user.domain.repository.RefreshTokenRepository;
import com.example.oops.cofig.security.Dto.TokenInfo;
import com.example.oops.cofig.security.provider.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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

    @PostMapping("/sign")
    public ResponseEntity<String> sign(@RequestBody SignRequestDto signRequestDto) {
        signService.Sign(signRequestDto.getUsername(), signRequestDto.getPassword());
        return ResponseEntity.ok().body("Success");
    }


    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        loginService.login(loginRequestDto,response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenInfo> refreshToken(@RequestBody RefreshRequestDto refreshRequestDto) {
       return ResponseEntity.ok(loginService.refreshToken(refreshRequestDto));
    }


}
