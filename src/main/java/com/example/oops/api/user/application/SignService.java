package com.example.oops.api.user.application;


import com.example.oops.api.user.domain.User;
import com.example.oops.api.user.dto.SignRequestDto;
import com.example.oops.api.user.repository.UserRepository;
import com.example.oops.common.error.ErrorCode;
import com.example.oops.common.error.OopsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class SignService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;



    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLoginInfo_Username(username).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public User Sign(SignRequestDto signRequestDto) {
        if(userRepository.existsByLoginInfo_Username(signRequestDto.getUsername())) {
            throw new OopsException(ErrorCode.DUPLICATE_USERNAME);
        }

        String hashedPassword = passwordEncoder.encode(signRequestDto.getPassword());

        User newUser= User.of(hashedPassword,signRequestDto);



        return userRepository.save(newUser);
    }


    public User findByUserName(String userName){
        return loadUserByUsername(userName);
    }
}
