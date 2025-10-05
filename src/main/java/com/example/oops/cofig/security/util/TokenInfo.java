package com.example.oops.cofig.security.util;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo {
    private String grantType;
    private String refreshToken;
    private String accessToken;
    private Long refreshExpiresIn;
}
