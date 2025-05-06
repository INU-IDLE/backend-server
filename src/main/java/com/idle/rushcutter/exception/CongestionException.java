package com.idle.rushcutter.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CongestionException extends RuntimeException {
    private final HttpStatus httpStatus;

    public CongestionException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
