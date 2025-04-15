package com.idle.rushcutter.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PathException.class)
    public ResponseEntity<Map<String, String>> handlePathException(PathException e) {
        HttpStatus status = e.getHttpStatus();
        return ResponseEntity
                .status(status)
                .body(Map.of(
                        "message", e.getMessage(),
                        "result", "null"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOther(Exception e) {
        log.error("[예기치 못한 예외 발생] message={}, stackTrace={}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "message", "예기치 못한 오류가 발생했습니다.",
                        "result", "null"
                ));
    }
}
