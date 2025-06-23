package com.idle.rushcutter.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CongestionException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final int code;
    private final String responseMessage;

    public CongestionException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = -1;
        this.responseMessage = message;
    }

    public CongestionException(String message, HttpStatus httpStatus, int code, String responseMessage) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
        this.responseMessage = responseMessage;
    }
}
