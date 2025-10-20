package com.example.oops.api.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class EmailSendRequestDto {

    @NotBlank(message = "이메일 형식 입력값 필수 입니다.")
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;

}
