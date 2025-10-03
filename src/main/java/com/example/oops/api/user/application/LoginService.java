package com.example.oops.api.user.domain.application;

import com.example.oops.CookieUtil;
import com.example.oops.api.user.domain.RefreshToken;
import com.example.oops.api.user.domain.User;
import com.example.oops.api.user.domain.dto.LoginRequestDto;
import com.example.oops.api.user.domain.repository.RefreshRequestDto;
import com.example.oops.api.user.domain.repository.RefreshTokenRepository;
import com.example.oops.api.user.domain.repository.UserRepository;
import com.example.oops.cofig.security.Dto.TokenInfo;
import com.example.oops.cofig.security.provider.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class LoginService  {
    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;

    private final RefreshTokenRepository refreshTokenRepository;

    private final CookieUtil cookieUtil;

    private final SignService signService;

    @Transactional
    public void login(LoginRequestDto loginRequestDto, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String username = userDetails.getUsername();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        TokenInfo tokenInfo = jwtTokenProvider.createToken(username, roles);

        response.setHeader("Authorization", "Bearer " + tokenInfo.getAccessToken());
        cookieUtil.addRefreshToken(
                response,
                tokenInfo.getRefreshToken(),
                tokenInfo.getRefreshExpiresIn()/1000
        );

        RefreshToken refreshToken = RefreshToken.builder()
                .expiryTime(tokenInfo.getRefreshExpiresIn())
                .build();
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public TokenInfo refreshToken(RefreshRequestDto refreshRequestDto) {
        if(!jwtTokenProvider.validateToken(refreshRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        String userName = jwtTokenProvider.getUserPk(refreshRequestDto.getRefreshToken());
        RefreshToken redisToken = refreshTokenRepository.findById(userName)
                .orElseThrow(() -> new RuntimeException("Refresh Token 정보가 Redis에 없습니다. (재로그인 필요)"));

        if(!redisToken.getToken().equals(refreshRequestDto.getRefreshToken())) {
            throw new RuntimeException("저장된 RefreshToken과 일치하지 않습니다.");
        }

        List<String> roles = signService.findByUserName(userName).getRoles();
        String newAccessToken = jwtTokenProvider.creatAccessToken(userName, roles);

          return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(newAccessToken)
                .refreshToken(refreshRequestDto.getRefreshToken())
                .refreshExpiresIn(redisToken.getExpiryTime())
                .build();
    }

}
