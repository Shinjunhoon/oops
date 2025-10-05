package com.example.oops.common.error;

import lombok.Getter;

@Getter
public class OopsException extends RuntimeException {

    private final ErrorCode errorCode;

    public OopsException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
