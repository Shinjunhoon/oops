package com.example.oops.cofig.web;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebSecurityConfig implements WebMvcConfigurer {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 요청 경로 허용
                .allowedOrigins("http://localhost:3000", "http://localhost:3000/","https://soccer-up-puwg.vercel.app/")  // 두 가지 모두 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // OPTIONS 요청도 허용
                .allowedHeaders("Authorization", "Content-Type") // Authorization 헤더와 Content-Type 허용
                .exposedHeaders("Authorization") // 클라이언트에서 확인할 수 있도록 노출
                .allowCredentials(true); // 쿠키, 인증정보 포함 허용
    }
}
