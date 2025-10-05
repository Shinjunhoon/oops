package com.example.oops.error;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // 400 Bad Request
    INVALID_VOTE_BOARD(HttpStatus.BAD_REQUEST, "VOTE-001", "투표는 토론 게시판에서만 가능합니다."),

    // 404 Not Found
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST-001", "요청된 게시글을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-001", "인증된 사용자를 찾을 수 없습니다."),

    // 500 Internal Server Error (비상용)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER-999", "서버 처리 중 예상치 못한 오류가 발생했습니다.");
    private final HttpStatus status;
    private final String code;
    private final String message;
}
