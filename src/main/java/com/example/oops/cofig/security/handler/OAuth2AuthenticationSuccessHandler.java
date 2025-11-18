package com.example.oops.cofig.security.handler;

import com.example.oops.cofig.security.provider.JwtTokenProvider;
import com.example.oops.cofig.security.util.TokenInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${oauth2.authorized-redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        // 1. JWT 토큰 생성
        TokenInfo tokenInfo = jwtTokenProvider.createToken(authentication);
        String accessToken = tokenInfo.getAccessToken();
        String refreshToken = tokenInfo.getRefreshToken();

        // ⭐️ 2. Refresh Token을 HTTP Only 쿠키로 설정 (보안 강화)
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                // Access Token 만료 시간보다 길게 설정해야 합니다. (JwtTokenProvider에서 가져오는 것이 이상적)
                .maxAge(60 * 60 * 24 * 7) // 예시: 7일 설정 (실제 값으로 대체 필요)
                .path("/")
                .secure(true) // ⚠️ HTTPS 환경에서만 전송 (운영 시 필수)
                .httpOnly(true) // ⭐️ JavaScript 접근 방지
                .sameSite("Lax") // CSRF 방지
                .build();

        // 쿠키를 응답 헤더에 추가
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // ⭐️ 3. Access Token만 URL 파라미터에 담아 프론트엔드로 리디렉션
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("accessToken", accessToken)
                // ⚠️ Refresh Token 쿼리 파라미터 제거
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String extractEmail(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();
            return (String) attributes.get("email");
        }
        return null;
    }
}