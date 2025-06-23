package com.idle.rushcutter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idle.rushcutter.dto.congestion.CongestionInfo;
import com.idle.rushcutter.dto.congestion.CongestionResponseDto;
import com.idle.rushcutter.dto.congestion.RealTimeCongestionResponseDto;
import com.idle.rushcutter.enums.CongestionLevel;
import com.idle.rushcutter.exception.CongestionException;
import com.idle.rushcutter.util.CongestionParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CongestionService {

    @Value("${congestion.api.base-url}")
    private String congestionApiBaseUrl;

    @Value("${sk.openapi.appkey}")
    private String appKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public CongestionResponseDto getPredictedCongestion(String stationCode, int line, int updnLine, LocalDateTime dateTime, String dayType) {
        if (line < 2 || line > 8) {
            throw new CongestionException("현재 혼잡도 예측은 2~8호선만 지원됩니다.", HttpStatus.BAD_REQUEST);
        }

        log.info("dayType raw value: {}", dayType);
        String url = String.format(
                "%s/api/v1/congestion/real-time/car/%s?line=%d&updnLine=%d&time_slot=%s&weekday_type=%s&month=%d",
                congestionApiBaseUrl,
                stationCode,
                line,
                updnLine,
                String.format("%02d%02d", dateTime.getHour(), dateTime.getMinute()),
                dayType,
                dateTime.getMonthValue()
        );

        log.info("[ML 서버 요청 디버깅] Request URL: {}", url);
        log.info("[ML 서버 요청 디버깅] stationCode={}, timeSlot={}, updnLine={}, dayType={}, line={}, month={}",
            stationCode,
            String.format("%02d%02d", dateTime.getHour(), dateTime.getMinute()),
            updnLine,
            dayType,
            line,
            dateTime.getMonthValue());

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        } catch (ResourceAccessException e) {
            throw new CongestionException("혼잡도 예측 서버에 연결할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, CongestionInfo> predictions = new HashMap<>();
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode predNode = root.get("predictions");

            for (int i = 1; i <= 10; i++) {
                String key = "car_" + i;
                if (predNode.has(key)) {
                    double percent = predNode.get(key).asDouble();
                    predictions.put(key, new CongestionInfo(CongestionLevel.fromPercentage(percent), percent));
                }
            }

            return new CongestionResponseDto(
                    stationCode,
                    dateTime,
                    updnLine,
                    dayType,
                    line,
                    predictions
            );
        } catch (Exception e) {
            throw new CongestionException("혼잡도 예측 결과 처리 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public RealTimeCongestionResponseDto getRealTimeCongestion(String lineName, String trainNumber) {
        // Normalize train number to SK format: lineName + last 3 digits of trainNumber
        trainNumber = lineName + String.format("%03d", Integer.parseInt(trainNumber) % 1000);
        String redisKey = "subway:congestion:" + lineName + ":" + trainNumber;
        log.info("[Redis] 캐시 조회 시도 - key={}", redisKey);
        String cached = redisTemplate.opsForValue().get(redisKey);
        JsonNode response;

        if (cached != null) {
            log.info("[Redis] 캐시 HIT - key={}", redisKey);
            try {
                response = objectMapper.readTree(cached);
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                throw new CongestionException("Redis 캐시 파싱 실패", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.info("[SK oepn API] 요청 전송: 실시간 열차 혼잡도 요청 - lineName={}, trainNumber={}", lineName, trainNumber);
            String url = "https://apis.openapi.sk.com/puzzle/subway/congestion/rltm/trains/" + lineName + "/" + trainNumber;
            HttpHeaders headers = new HttpHeaders();
            headers.set("appKey", appKey);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> apiResponse;
            try {
                apiResponse = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            } catch (HttpClientErrorException e) {
                String body = e.getResponseBodyAsString();
                try {
                    JsonNode errorResponse = objectMapper.readTree(body);
                    int code = errorResponse.path("code").asInt(-1);
                    String msg = errorResponse.path("msg").asText("SK API 오류 응답");

                    log.error("[SK oepn API] 오류 응답 수신 - status={}, code={}, msg={}", e.getStatusCode(), code, msg);
                    throw new CongestionException("SK API 호출 실패", HttpStatus.BAD_REQUEST, code, msg);
                } catch (JsonProcessingException ex) {
                    throw new CongestionException("SK API 오류 응답 파싱 실패", HttpStatus.BAD_REQUEST);
                }
            }
            String body = apiResponse.getBody();
            log.info("[SK oepn API] 응답 수신: status={}, body={}", apiResponse.getStatusCode(), body);

            if (body == null) {
                throw new CongestionException("SK OpenAPI 응답이 비어 있습니다", HttpStatus.BAD_GATEWAY);
            }
            try {
                response = objectMapper.readTree(body);
            } catch (JsonProcessingException e) {
                throw new CongestionException("SK OpenAPI 응답 파싱 실패", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if (!response.path("success").asBoolean()) {
                String msg = response.path("msg").asText();
                int code = response.path("code").asInt();
                log.error("[SK oepn API] 호출 실패 - code={}, msg={}", code, msg);
                throw new CongestionException("SK API 호출 실패", HttpStatus.BAD_REQUEST, code, msg);
            }

            // TTL: 60s
            redisTemplate.opsForValue().set(redisKey, body, Duration.ofSeconds(60));
            log.info("[Redis] 캐시 MISS - SK OpenAPI 응답을 캐싱함 - key={}, TTL={}s", redisKey, 60);
        }

        JsonNode result = response.path("data").path("congestionResult");
        String congestionCar = result.path("congestionCar").asText();
        int congestionType = result.path("congestionType").asInt();

        Map<String, CongestionInfo> cars = CongestionParser.parse(congestionCar);
        return new RealTimeCongestionResponseDto(lineName, trainNumber, congestionType, cars);
    }
}
