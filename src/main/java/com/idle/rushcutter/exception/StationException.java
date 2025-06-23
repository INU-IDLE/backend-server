package com.idle.rushcutter.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class StationException extends RuntimeException {
    private final HttpStatus httpStatus;

    public StationException(String message) {
        super(message);
        this.httpStatus = HttpStatus.NOT_FOUND;
    }

    public StationException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
