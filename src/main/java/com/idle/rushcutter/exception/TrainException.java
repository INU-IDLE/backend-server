package com.idle.rushcutter.exception;

import org.springframework.http.HttpStatus;

public class TrainException extends RuntimeException {
    private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public TrainException(String message) {
        super(message);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
