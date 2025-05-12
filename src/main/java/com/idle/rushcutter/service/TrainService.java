package com.idle.rushcutter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idle.rushcutter.dto.train.TrainPositionDto;
import com.idle.rushcutter.dto.train.TrainPositionResponseDto;
import com.idle.rushcutter.enums.ExpressType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrainService {

    @Value("${openapi.seoul.key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public TrainPositionResponseDto getTrainPositions(String lineCode) {
        try {
            String decodedLineCode = java.net.URLDecoder.decode(lineCode, StandardCharsets.UTF_8);
            List<String> supportedLineCodes = List.of(
                "1호선", "2호선", "3호선", "4호선", "5호선", "6호선", "7호선", "8호선", "9호선",
                "공항철도", "경의중앙선", "경춘선", "수인분당선", "신분당선", "경강선", "서해선", "우이신설선", "GTX-A"
            );
            if (!supportedLineCodes.contains(decodedLineCode)) {
                throw new com.idle.rushcutter.exception.TrainException("지원하지 않는 노선입니다: " + decodedLineCode);
            }
            String url = String.format(
                    "http://swopenAPI.seoul.go.kr/api/subway/%s/json/realtimePosition/0/1000/%s",
                    apiKey, decodedLineCode
            );
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            if (!"INFO-000".equals(root.path("errorMessage").path("code").asText())) {
                return new TrainPositionResponseDto("API 응답 오류", List.of());
            }

            List<TrainPositionDto> trainPositions = new ArrayList<>();
            for (JsonNode item : root.path("realtimePositionList")) {
                String trainNo = item.path("trainNo").asText();
                String statnNm = item.path("statnNm").asText();
                String statnTnm = item.path("statnTnm").asText();
                String recptnDt = item.path("recptnDt").asText();

                String direction = "0".equals(item.path("updnLine").asText()) ? "상행" : "하행";

                String status = switch (item.path("trainSttus").asInt()) {
                    case 0 -> "진입";
                    case 1 -> "도착";
                    case 2 -> "출발";
                    case 3 -> "전역출발";
                    default -> "알수없음";
                };

                String expressType = ExpressType.fromCode(item.path("directAt").asInt()).getDescription();

                boolean isLastTrain = "1".equals(item.path("lstcarAt").asText());

                int rowNum = item.path("rowNum").asInt();
                int totalCount = item.path("totalCount").asInt();
                trainPositions.add(new TrainPositionDto(
                        trainNo,
                        statnNm,
                        direction,
                        status,
                        expressType,
                        statnTnm,
                        isLastTrain,
                        recptnDt,
                        rowNum,
                        totalCount
                ));
            }

            return new TrainPositionResponseDto("열차 위치 조회 성공", trainPositions);

        } catch (IOException e) {
            return new TrainPositionResponseDto("응답 파싱 오류", List.of());
        } catch (com.idle.rushcutter.exception.TrainException e) {
            throw e;
        } catch (Exception e) {
            return new TrainPositionResponseDto("API 요청 실패", List.of());
        }
    }
}