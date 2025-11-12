package com.example.oops.api.user.application.impl;

import com.example.oops.api.user.domain.User;
import com.example.oops.api.user.dto.UserIdCheckRequestDto;
import com.example.oops.api.user.dto.UserInfoDto;

public interface UserService {
    public UserInfoDto getUserInfo(Long userId);
    public boolean checkUserId(UserIdCheckRequestDto requestDto);
    public String deleteUser(Long userId);
}
