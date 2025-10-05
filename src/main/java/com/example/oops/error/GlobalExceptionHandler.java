package com.example.oops.error;


import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OopsException.class)
    public ResponseEntity<String> handleBusinessException(OopsException ex) {
        log.warn("Business Exception: {} - {}", ex.getErrorCode().getCode(), ex.getMessage(),ex.getErrorCode());

        // 1. ErrorCode에 정의된 HTTP 상태 코드를 사용
        // 2. 응답 본문에는 ErrorCode의 메시지(혹은 JSON 객체)를 사용
        return new ResponseEntity<>(
                ex.getMessage(), // 간단하게 메시지만 반환
                ex.getErrorCode().getStatus()
        );
    }
}
