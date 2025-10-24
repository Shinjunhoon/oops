package com.example.oops.api.user.application;

import com.example.oops.api.user.domain.User;
import com.example.oops.api.user.dto.UserIdCheckRequestDto;
import com.example.oops.api.user.dto.UserInfoDto;
import com.example.oops.api.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class UserService implements com.example.oops.api.user.application.impl.UserService {

    private final UserRepository userRepository;

    @Override
    public UserInfoDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return new UserInfoDto(Objects.requireNonNull(user).getUserInfo());
    }

    @Override
    public boolean checkUserId(UserIdCheckRequestDto userIdCheckRequestDto) {
        return !userRepository.existsByLoginInfo_Username(userIdCheckRequestDto.getUserName());

    }
}
