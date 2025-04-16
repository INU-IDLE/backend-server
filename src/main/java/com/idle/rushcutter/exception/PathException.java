package com.idle.rushcutter.exception;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class PathException extends RuntimeException {
    private final HttpStatus httpStatus;

    public PathException(String message) {
        super(message);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public PathException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public PathException(JsonNode errorResponse) {
        super(extractMessage(errorResponse));
        this.httpStatus = determineHttpStatus(errorResponse);
    }

    private static String extractMessage(JsonNode errorResponse) {
        log.warn("[ODsay API 오류 응답] raw = {}", errorResponse.toPrettyString());
        if (errorResponse.has("error")) {
            JsonNode errorNode = errorResponse.get("error");

            // Case 1: error is an object
            if (errorNode.isObject()) {
                String code = errorNode.has("code") ? errorNode.get("code").asText() : "unknown";
                String msg = errorNode.has("msg") ? errorNode.get("msg").asText() : "unknown error";
                return String.format("{code:%s, message:%s}", code, msg);
            }

            // Case 2: error is an array
            if (errorNode.isArray() && !errorNode.isEmpty()) {
                JsonNode first = errorNode.get(0);
                String code = first.has("code") ? first.get("code").asText() : "unknown";
                String msg = first.has("message") ? first.get("message").asText() : "unknown error";
                return String.format("{code:%s, message:%s}", code, msg);
            }
        }
        return "알 수 없는 오류 발생";
    }

    private static HttpStatus determineHttpStatus(JsonNode errorResponse) {
        log.warn("[PathException] 상태 코드 판단용 errorResponse = {}", errorResponse.toPrettyString());
        if (errorResponse.has("error")) {
            JsonNode errorNode = errorResponse.get("error");
            JsonNode nodeToCheck = errorNode.isArray() && !errorNode.isEmpty() ? errorNode.get(0) : errorNode;

            if (nodeToCheck.isObject() && nodeToCheck.has("code")) {
                String code = nodeToCheck.get("code").asText().trim();
                if ("-99".equals(code)) {
                    log.warn("[PathException] 인식된 에러 코드 -99: 검색결과 없음, HTTP 400으로 처리합니다.");
                    return HttpStatus.BAD_REQUEST;
                }
                if ("500".equals(code)) return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
