package com.example.oops.api.user.dto;


import com.example.oops.api.user.domain.enums.Line;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SignRequestDto {

    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    @NotEmpty
    private String password;


    @NotNull
    private Line line;

    @NotNull
    private String nickname;


}
