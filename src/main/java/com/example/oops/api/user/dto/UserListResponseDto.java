package com.example.oops.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class UserListResponseDto {
    private String userName;
    private String email;
    private Long userId;
}
