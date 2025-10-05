package com.example.oops.api.user.repository;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RefreshRequestDto {
    @NotNull
    private String refreshToken;
}
