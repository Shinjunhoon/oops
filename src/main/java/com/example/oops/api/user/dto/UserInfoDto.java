package com.example.oops.api.user.dto;


import com.example.oops.api.user.domain.enums.Line;
import com.example.oops.api.user.domain.value.UserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserInfoDto {
    String nickname;
    Line line;


    public UserInfoDto(UserInfo userInfo) {
        nickname = userInfo.getNickname();
        line = userInfo.getLine();
    }
}
