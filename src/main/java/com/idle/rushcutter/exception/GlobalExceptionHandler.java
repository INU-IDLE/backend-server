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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOther(Exception e) {
        log.error("[예기치 못한 예외 발생] message={}, stackTrace={}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "message", "예기치 못한 오류가 발생했습니다.",
                        "result", "null"
                ));
    }

    @ExceptionHandler(PathException.class)
    public ResponseEntity<Map<String, String>> handlePathException(PathException e) {
        log.warn("[경로 탐색 오류] message={}", e.getMessage());
        HttpStatus status = e.getHttpStatus();
        return ResponseEntity
                .status(status)
                .body(Map.of(
                        "message", e.getMessage(),
                        "result", "null"
                ));
    }

    @ExceptionHandler(StationException.class)
    public ResponseEntity<Map<String, String>> handleStationException(StationException e) {
        log.warn("[역 오류] message={}", e.getMessage());
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(Map.of(
                        "message", e.getMessage(),
                        "result", "null"
                ));
    }

    @ExceptionHandler(CongestionException.class)
    public ResponseEntity<Map<String, Object>> handleCongestionException(CongestionException e) {
        log.warn("[혼잡도 예측 오류] message={}", e.getMessage());
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(Map.of(
                        "code", e.getCode(),
                        "message", e.getResponseMessage()
                ));
    }

    @ExceptionHandler(TrainException.class)
    public ResponseEntity<Map<String, Object>> handleTrainException(TrainException e) {
        log.warn("[열차 위치 오류] message={}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "message", e.getMessage(),
                        "result", "null"
                ));
    }
}
