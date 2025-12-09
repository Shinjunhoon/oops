package com.example.oops.common.error;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // 400 Bad Request
    INVALID_VOTE_BOARD(HttpStatus.BAD_REQUEST, "VOTE-001", "투표는 토론 게시판에서만 가능합니다."),
    REPORT_OWN_POST_FORBIDDEN(HttpStatus.FORBIDDEN, "R403", "본인이 작성한 게시물은 신고할 수 없습니다."),
    REPORT_REASON_TOO_SHORT(HttpStatus.BAD_REQUEST, "R001", "신고 상세 내용은 최소 10자 이상 입력해야 합니다."),
    DUPLICATE_REPORT(HttpStatus.CONFLICT, "R409", "이미 해당 게시물을 신고했습니다. (중복 신고 불가)"),
    INVALID_REPORT_TYPE(HttpStatus.BAD_REQUEST, "R002", "유효하지 않은 신고 유형입니다."),

    // 404 Not Found
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST-001", "요청된 게시글을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-001", "인증된 사용자를 찾을 수 없습니다."),

    // 500 Internal Server Error (비상용)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER-999", "서버 처리 중 예상치 못한 오류가 발생했습니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "USER-002", "이미 존재하는 아이디입니다. 다른 아이디를 사용해주세요."),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH-001", "제공된 Refresh Token이 유효하지 않거나 위변조되었습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH-002", "유효한 Refresh Token 정보가 서버에 없습니다. 재로그인이 필요합니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "AUTH-003", "요청 토큰과 서버에 저장된 토큰이 일치하지 않습니다."),
    NO_AUTHORITY(HttpStatus.FORBIDDEN, "AUTH-403", "해당 작업을 수행할 권한이 없습니다."), // ✨ 추가된 코드
    RIOT_API_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH-44","Riot API에서 요청한 리소스를 찾을 수 없습니다."),
    RIOT_API_RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS,"AUTH-44","Riot API 호출 제한을 초과했습니다. 잠시 후 다시 시도해 주세요."),
    RIOT_API_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-44","Riot API 서버 내부 오류입니다."),

    // 일반적인 클라이언트 에러
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "AUTH-44","요청 파라미터가 유효하지 않습니다."),
    REQUIRED_VIDEO_SOURCE_MISSING(HttpStatus.BAD_REQUEST,"POST-002","동영상이 없습니다"),
    COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST,"POST-002","댓글이 없습니다.");



    private final HttpStatus status;
    private final String code;
    private final String message;
}
