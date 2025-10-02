package com.example.oops.cofig.security.provider;

import com.example.oops.cofig.security.Dto.TokenInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import javax.xml.crypto.Data;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

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
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
    public String getUserPk(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token).getBody().getSubject();
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


    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (SecurityException | MalformedJwtException e) {
            // 잘못된 JWT 서명
        } catch (ExpiredJwtException e) {
            // 만료된 JWT 토큰
            return false; // 만료된 토큰은 무효
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 JWT 토큰
        } catch (IllegalArgumentException e) {
            // JWT 토큰이 잘못됨
        }
        return false;
    }
    }
