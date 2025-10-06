package com.example.oops.cofig.security.provider;

import com.example.oops.api.user.domain.User;
import com.example.oops.cofig.security.util.TokenInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {


    private final String secretKey = "YourVerySecureAndLongSecretKeyForJWTsThatShouldBeAtLeast32Bytes";

    // 1시간
    private final Long accessTokenValidityInSeconds = 60* 60* 1000L;

    // 2주
    private final Long refreshTokenValidityInSeconds = 14 * 24 * 60 * 60 * 1000L;

    private Key key;
    private final UserDetailsService userDetailsService;

    // 바이트 배열로 변환 후 HMAC-SHA 서명용 Key 객체 생성
    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public TokenInfo createToken(String userPk, List<String> roles){
        String accessToken = creatAccessToken(userPk, roles);
        String refreshToken = createRefreshToken(userPk);

        return TokenInfo.builder()
                .grantType("bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshExpiresIn(refreshTokenValidityInSeconds)
                .build();
    }

    public String creatAccessToken  (String userPk, List<String> roles){
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("roles", roles);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+ accessTokenValidityInSeconds))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String userPk){
        Claims claims = Jwts.claims().setSubject(userPk);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+ refreshTokenValidityInSeconds))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token){
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        log.info("userDetails: {}", userDetails);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
    public String getUserPk(String token) {
        try {
            String userName =Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token).getBody().getSubject();
            log.info("userName: {}", userName);
            return userName;
        } catch (ExpiredJwtException e) {
            // 토큰이 만료되었더라도 subject(userPk)는 복호화 가능
            return e.getClaims().getSubject();
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public long getLoginId(final Authentication authentication) throws AccessDeniedException {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("로그인한 사용자만 접근할 수 있습니다.");
        }

        User user = (User) authentication.getPrincipal();
        return user.getId();  // ✅ userId 반환
    }



    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("잘못된 JWT 토큰: {}", e.getMessage());
        }
        return false;
    }

}
