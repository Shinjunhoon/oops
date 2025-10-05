package com.example.oops.common;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Builder
@Getter
public class ApiResponseEntity<T extends Object> {

private final String message;
private final T data;
public ApiResponseEntity(String message, T data) {
    this.message = message;
    this.data = data;
}


    public static <T> ResponseEntity<ApiResponseEntity> successResponseEntity(T data) {
        return ResponseEntity.ok(
                ApiResponseEntity.builder()
                        .data(data)
                        .message("요청 성공")
                        .build()
        );
    }



}
