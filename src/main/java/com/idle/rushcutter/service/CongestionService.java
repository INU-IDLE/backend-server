package com.idle.rushcutter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idle.rushcutter.dto.congestion.CongestionInfo;
import com.idle.rushcutter.dto.congestion.CongestionResponseDto;
import com.idle.rushcutter.enums.CongestionLevel;
import com.idle.rushcutter.exception.CongestionException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CongestionService {

    @Value("${congestion.api.base-url}")
    private String congestionApiBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public CongestionResponseDto getPredictedCongestion(int stationCode, int line, String updnLine, LocalDateTime dateTime, String dayType) {
        if (line < 2 || line > 8) {
            throw new CongestionException("현재 혼잡도 예측은 2~8호선만 지원됩니다.", HttpStatus.BAD_REQUEST);
        }

        String url = UriComponentsBuilder.fromHttpUrl(congestionApiBaseUrl + "/api/v1/congestion/real-time/car/" + stationCode)
                .queryParam("line", line)
                .queryParam("updnLine", updnLine)
                .queryParam("time_slot", String.format("%02d%02d", dateTime.getHour(), dateTime.getMinute()))
                .queryParam("weekday_type", dayType)
                .queryParam("month", dateTime.getMonthValue())
                .toUriString();

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
}
