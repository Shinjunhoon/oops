package com.example.oops.common;

import com.example.oops.api.user.domain.User;
import com.example.oops.api.user.domain.enums.Line;
import com.example.oops.api.user.domain.value.LoginInfo;
import com.example.oops.api.user.domain.value.UserInfo;
import com.example.oops.api.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Configuration
public class AdminInitializer {
    private static final String ADMIN_USERNAME = "admin";
    private static final String REGULAR_USERNAME = "user";
    private static final String REGULAR_USERNAME2 = "user2";
    private static final String REGULAR_USERNAME3 = "user3";
    private static final String INITIAL_PASSWORD = "1234";
    private static final String INITIAL_PASSWOR = "1234";

    @Bean
    public CommandLineRunner initAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) { // 비밀번호 해시를 위한 PasswordEncoder 주입

        return args -> {
            // =============================================================
            // 1. 관리자 계정 초기화 (admin)
            // =============================================================
            if (userRepository.findByLoginInfo_Username(ADMIN_USERNAME).isEmpty()) {

                LoginInfo adminLoginInfo = LoginInfo.builder()
                        .username(ADMIN_USERNAME)
                        .password(passwordEncoder.encode(INITIAL_PASSWORD)) // 비밀번호 해시
                        .roles(Collections.singletonList("ROLE_ADMIN")) // 관리자 권한 부여
                        .build();

                UserInfo adminUserInfo = UserInfo.builder()
                        .line(Line.BOTTOM)
                        .nickname(ADMIN_USERNAME)
                        .build();

                User adminUser = User.builder()
                        .loginInfo(adminLoginInfo)
                        .userInfo(adminUserInfo)
                        .build();

                userRepository.save(adminUser);
                System.out.println("✅ 초기 관리자 계정(" + ADMIN_USERNAME + ")이 성공적으로 생성되었습니다. (PW: " + INITIAL_PASSWORD + ")");
            }

            // =============================================================
            // 2. 일반 사용자 계정 초기화 (user)
            // =============================================================
            if (userRepository.findByLoginInfo_Username(REGULAR_USERNAME).isEmpty()) {

                LoginInfo regularLoginInfo = LoginInfo.builder()
                        .username(REGULAR_USERNAME)
                        .password(passwordEncoder.encode(INITIAL_PASSWOR)) // 비밀번호 해시
                        .roles(Collections.singletonList("ROLE_USER")) // 일반 사용자 권한 부여
                        .build();

                UserInfo regularUserInfo = UserInfo.builder()
                        .line(Line.TOP) // 관리자와 다른 라인으로 설정
                        .nickname(REGULAR_USERNAME)
                        .build();

                User regularUser = User.builder()
                        .loginInfo(regularLoginInfo)
                        .userInfo(regularUserInfo)
                        .build();

                userRepository.save(regularUser);
                System.out.println("✅ 초기 일반 사용자 계정(" + REGULAR_USERNAME + ")이 성공적으로 생성되었습니다. (PW: " + INITIAL_PASSWORD + ")");
            }

            // =============================================================
            if (userRepository.findByLoginInfo_Username(REGULAR_USERNAME2).isEmpty()) {

                LoginInfo regularLoginInfo = LoginInfo.builder()
                        .username(REGULAR_USERNAME2)
                        .password(passwordEncoder.encode(INITIAL_PASSWOR)) // 비밀번호 해시
                        .roles(Collections.singletonList("ROLE_USER")) // 일반 사용자 권한 부여
                        .build();

                UserInfo regularUserInfo = UserInfo.builder()
                        .line(Line.TOP) // 관리자와 다른 라인으로 설정
                        .nickname(REGULAR_USERNAME2)
                        .build();

                User regularUser = User.builder()
                        .loginInfo(regularLoginInfo)
                        .userInfo(regularUserInfo)
                        .build();

                userRepository.save(regularUser);
                System.out.println("✅ 초기 일반 사용자 계정(" + REGULAR_USERNAME2 + ")이 성공적으로 생성되었습니다. (PW: " + INITIAL_PASSWORD + ")");
            }
            if (userRepository.findByLoginInfo_Username(REGULAR_USERNAME3).isEmpty()) {

                LoginInfo regularLoginInfo = LoginInfo.builder()
                        .username(REGULAR_USERNAME3)
                        .password(passwordEncoder.encode(INITIAL_PASSWOR)) // 비밀번호 해시
                        .roles(Collections.singletonList("ROLE_USER")) // 일반 사용자 권한 부여
                        .build();

                UserInfo regularUserInfo = UserInfo.builder()
                        .line(Line.TOP) // 관리자와 다른 라인으로 설정
                        .nickname(REGULAR_USERNAME3)
                        .build();

                User regularUser = User.builder()
                        .loginInfo(regularLoginInfo)
                        .userInfo(regularUserInfo)
                        .build();

                userRepository.save(regularUser);
                System.out.println("✅ 초기 일반 사용자 계정(" + REGULAR_USERNAME3 + ")이 성공적으로 생성되었습니다. (PW: " + INITIAL_PASSWORD + ")");
            }
        };
    }
}