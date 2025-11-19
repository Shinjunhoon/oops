package com.example.oops.cofig.security.provider;

import com.example.oops.api.user.domain.User;
import com.example.oops.api.user.repository.UserRepository;
import com.example.oops.cofig.security.util.TokenInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    private final UserRepository userRepository;

    @Value("${spring.jwt.secret}")
    private  String secretKey;

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

    public TokenInfo oauthCreateToken(String userPk, List<String> roles,Long userId, String nickname, String lolPosition){
        String accessToken = oauthCreatAccessToken(userPk, roles, userId, nickname, lolPosition);
        String refreshToken = createRefreshToken(userPk);

        return TokenInfo.builder()
                .grantType("bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshExpiresIn(refreshTokenValidityInSeconds)
                .build();
    }
    public TokenInfo createToken(Authentication authentication) {
        // UserDetails에서 userPk (username/email) 추출
        String userPk = authentication.getName();

        User user = userRepository.findBySocialId(userPk).orElseThrow(() -> new UsernameNotFoundException("User not found: " + userPk));

        String nickname = user.getUserInfo().getNickname();
        String lolPosition = String.valueOf(user.getUserInfo().getLine());
        Long userId = user.getId();
        // 역할 추출
        List<String> roles = this.extractRoles(authentication);

        return oauthCreateToken(userPk, roles, userId, nickname, lolPosition);
    }

    public String oauthCreatAccessToken  (String userPk, List<String> roles,Long userId, String nickname, String lolPosition){
        Claims claims = Jwts.claims().setSubject(userPk);
        claims.put("roles", roles);
        claims.put("userId", userId);
        claims.put("nickname", nickname);
        claims.put("lolPosition", lolPosition);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+ accessTokenValidityInSeconds))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ⭐️ 추가: Authentication 객체에서 역할을 List<String>으로 추출하는 메서드
    public List<String> extractRoles(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toList());
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
        log.info("bearerToken: {}", bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            log.info("bearerToken: {}", bearerToken);
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

    public long getRemainingExpirationTime(String token) {
        try {
            // 토큰을 파싱하지 않고 바로 만료 시간만 가져옵니다.
            // validateToken에서 이미 ExpiredJwtException을 잡으므로, 여기서는 단순히 만료 시간을 추출합니다.
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            // 현재 시간과 만료 시간의 차이를 계산
            long now = System.currentTimeMillis();
            long remainingTime = expiration.getTime() - now;

            return Math.max(0, remainingTime); // 남은 시간이 음수가 되지 않도록 0 이상을 반환

        } catch (ExpiredJwtException e) {
            // 이미 만료된 토큰의 경우, TTL은 0이 되어야 합니다.
            // ExpiredJwtException에서는 getClaims()로 만료된 클레임에 접근 가능합니다.
            Date expiration = e.getClaims().getExpiration();
            long now = System.currentTimeMillis();
            long remainingTime = expiration.getTime() - now;

            return Math.max(0, remainingTime);

        } catch (Exception e) {
            // 다른 파싱 오류 (서명 오류 등) 발생 시, 블랙리스트에 등록할 필요 없으므로 0 반환
            log.error("JWT 파싱 중 오류 발생: {}", e.getMessage());
            return 0;
        }
    }
}