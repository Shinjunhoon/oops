package com.example.oops.api.RiotApiService;

import org.springframework.http.HttpStatus;

public class RiotApiException extends RuntimeException{
    private final HttpStatus status;

    public RiotApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

