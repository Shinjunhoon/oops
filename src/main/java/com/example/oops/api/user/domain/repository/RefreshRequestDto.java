package com.example.oops.api.user.domain.repository;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RefreshRequestDto {
    @NotNull
    private String refreshToken;
}
