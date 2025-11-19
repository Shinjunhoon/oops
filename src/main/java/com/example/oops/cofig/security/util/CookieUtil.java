package com.example.oops.cofig.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    public static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";

    public void addRefreshToken(HttpServletResponse response, String refreshToken, Long expiresIn) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);

        cookie.setHttpOnly(true);

        cookie.setSecure(false);

        cookie.setMaxAge((int) Math.min(expiresIn, Integer.MAX_VALUE));
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    public void addAccessToken(HttpServletResponse response, String accessToken, Long expiresIn) {
        Cookie cookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, accessToken);

        cookie.setHttpOnly(true);

        cookie.setSecure(false);

        cookie.setMaxAge((int) Math.min(expiresIn, Integer.MAX_VALUE));
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    public String getRefreshTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // TODO: 운영 환경에서는 true
        cookie.setMaxAge(0); // 만료 시간을 0으로 설정하여 즉시 삭제
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
