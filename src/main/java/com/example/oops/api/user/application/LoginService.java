package com.example.oops.api.user.application;

import com.example.oops.cofig.security.util.CookieUtil;
import com.example.oops.api.user.domain.RefreshToken;
import com.example.oops.api.user.dto.LoginRequestDto;
import com.example.oops.api.user.repository.RefreshRequestDto;
import com.example.oops.api.user.repository.RefreshTokenRepository;
import com.example.oops.cofig.security.util.TokenInfo;
import com.example.oops.cofig.security.provider.JwtTokenProvider;
import com.example.oops.common.error.ErrorCode;
import com.example.oops.common.error.OopsException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
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
                .userName(username)
                .token(tokenInfo.getRefreshToken())
                .expiryTime(tokenInfo.getRefreshExpiresIn())
                .build();
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void refreshToken(RefreshRequestDto refreshRequestDto,HttpServletResponse response) {

        String providerToken = refreshRequestDto.getRefreshToken();

        if(!jwtTokenProvider.validateToken(providerToken)) {
            throw new OopsException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        String userName = jwtTokenProvider.getUserPk(providerToken);
        RefreshToken redisToken = refreshTokenRepository.findById(userName)
                .orElseThrow(() -> new OopsException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if(!redisToken.getToken().equals(refreshRequestDto.getRefreshToken())) {
            throw new OopsException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        refreshTokenRepository.deleteById(userName);

        List<String> roles = signService.findByUserName(userName).getLoginInfo().getRoles();
        TokenInfo tokenInfo = jwtTokenProvider.createToken(userName, roles);

        response.setHeader("Authorization", "Bearer " + tokenInfo.getAccessToken());
        cookieUtil.addRefreshToken(
                response,
                tokenInfo.getRefreshToken(),
                tokenInfo.getRefreshExpiresIn()/1000
        );

          RefreshToken refreshToken = RefreshToken.builder()
                  .userName(userName)
                  .token(tokenInfo.getRefreshToken())
                  .expiryTime(tokenInfo.getRefreshExpiresIn())
                  .build();

          refreshTokenRepository.save(refreshToken);
    }

}
