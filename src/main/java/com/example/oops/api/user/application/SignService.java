package com.example.oops.api.user.domain.application;


import com.example.oops.api.user.domain.RefreshToken;
import com.example.oops.api.user.domain.User;
import com.example.oops.api.user.domain.dto.LoginRequestDto;
import com.example.oops.api.user.domain.repository.RefreshTokenRepository;
import com.example.oops.api.user.domain.repository.UserRepository;
import com.example.oops.cofig.security.Dto.TokenInfo;
import com.example.oops.cofig.security.provider.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SignService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;



    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public User Sign(String userName, String password) {
        if(userRepository.existsByUsername(userName)) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        User newUser = User.builder()
                .username(userName)
                .password(passwordEncoder.encode(password))
                .roles(Collections.singletonList("ROLE_USER"))
                .build();

        return userRepository.save(newUser);
    }


    public User findByUserName(String userName){
        return loadUserByUsername(userName);
    }
}
