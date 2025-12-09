package com.example.oops.cofig.security;

import com.example.oops.api.user.application.CustomOAuth2UserService;
import com.example.oops.api.user.repository.AccessTokenBlacklistRepository;
import com.example.oops.cofig.security.filtter.JwtFilter;
import com.example.oops.cofig.security.handler.CustomAccessDeniedHandler;
import com.example.oops.cofig.security.handler.CustomAuthenticationEntryPoint;
import com.example.oops.cofig.security.handler.OAuth2AuthenticationSuccessHandler;
import com.example.oops.cofig.security.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig  {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000","https://oops.io.kr"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 세션 미사용 (Stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))



                // 예외 처리
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(customAuthenticationEntryPoint) // 401
                        .accessDeniedHandler(customAccessDeniedHandler) // 403
                )

                // URL별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin/users/count","/api/admin/getReport","/api/admin/posts/","/api/admin/getReportUser").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

                        .requestMatchers("/api/auth/login", "/api/auth/sign", "/api/auth/reissue","/api/post/getDesPost/**"
                        ,"api/auth/refresh","/api/post/get/**","/api/auth/email","/api/auth/emailVerify","api/auth/checkUserName"
                        ,"/api/post/test","/api/post/get/","/api/post/get/**","/api/comment/createComment","/api/v1/account/player-details/**","/api/post/get/","/api/post/get/MovUpList").permitAll()


                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                // 1. 소셜에서 가져온 사용자 정보를 처리할 서비스 등록 (회원가입/로그인 처리 담당)
                                .userService(customOAuth2UserService)
                        )
                        // 2. 로그인 성공 시 커스텀 핸들러 사용 -> JWT 발행 및 리디렉션
                        .successHandler(oauth2AuthenticationSuccessHandler)
                )
                // JWT 필터 적용
                .addFilterBefore(new JwtFilter(jwtTokenProvider,accessTokenBlacklistRepository),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
