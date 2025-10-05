package com.example.oops.api.user.controller;


import com.example.oops.api.user.application.LoginService;
import com.example.oops.api.user.application.SignService;
import com.example.oops.api.user.dto.LoginRequestDto;
import com.example.oops.api.user.dto.SignRequestDto;
import com.example.oops.api.user.repository.RefreshRequestDto;
import com.example.oops.cofig.security.util.TokenInfo;
import jakarta.servlet.http.HttpServletResponse;
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
