package com.example.oops.cofig.security.filtter;

import com.example.oops.api.user.repository.AccessTokenBlacklistRepository;
import com.example.oops.cofig.security.provider.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AccessTokenBlacklistRepository accessTokenBlacklistRepository; // 👈 이 필드를 추가

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);

        log.info("token: {}", token);

        if (token != null) {
            try{
                // 1. 토큰 유효성 검사 (서명, 만료 시간)
                if(jwtTokenProvider.validateToken(token)){

                    // 2. 블랙리스트 확인
                    if (accessTokenBlacklistRepository.isBlacklisted(token)) {
                        log.warn("Logout token is used, denying authentication for token: {}", token);
                        // SecurityContextHolder에 인증 정보를 넣지 않고 바로 필터 체인을 계속 진행합니다.
                        // 이로 인해 요청은 '인증되지 않은 상태'로 다음 필터에 도달하여 401 처리가 됩니다.
                    } else {
                        // 3. 인증 성공: 블랙리스트가 아닐 경우에만 인증 객체를 설정
                        Authentication auth = jwtTokenProvider.getAuthentication(token);
                        log.info("auth: {}", auth);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        log.info("current user: {}", auth.getPrincipal());
                    }
                }
            }catch (Exception e ){
                log.error("JWT 인증 실패: {}", e.getMessage());
                // 토큰 인증 실패 시 SecurityContextHolder를 비워줍니다 (선택 사항이나 권장)
                SecurityContextHolder.clearContext();
            }
        }

        // 👈 최종적으로 필터 체인을 딱 한 번만 실행합니다.
        filterChain.doFilter(request, response);
    }
}