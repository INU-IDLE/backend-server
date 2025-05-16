package com.idle.rushcutter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idle.rushcutter.dto.train.ArrivalDto;
import com.idle.rushcutter.dto.train.ArrivalResponseDto;
import com.idle.rushcutter.dto.train.TrainPositionDto;
import com.idle.rushcutter.dto.train.TrainPositionResponseDto;
import com.idle.rushcutter.enums.ExpressType;
import com.idle.rushcutter.exception.TrainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
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

    public ArrivalResponseDto getStationArrivals(String lineCode, String stationName) {
        try {
            log.info("[실시간 도착정보] 요청 - 노선: {}, 역명: {}", lineCode, stationName);
            String decodedLineCode = java.net.URLDecoder.decode(lineCode, StandardCharsets.UTF_8);
            String decodedStationName = java.net.URLDecoder.decode(stationName, StandardCharsets.UTF_8);
            List<String> supportedLineCodes = List.of(
                    "1호선", "2호선", "3호선", "4호선", "5호선", "6호선", "7호선", "8호선", "9호선",
                    "공항철도", "경의중앙선", "경춘선", "수인분당선", "신분당선", "경강선", "서해선", "우이신설선", "GTX-A"
            );
            if (!supportedLineCodes.contains(decodedLineCode)) {
                throw new TrainException("지원하지 않는 노선입니다: " + decodedLineCode);
            }

            String url = String.format(
                    "http://swopenAPI.seoul.go.kr/api/subway/%s/json/realtimeStationArrival/1/100/%s",
                    apiKey, decodedStationName
            );

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            String responseCode = root.has("errorMessage") ?
                    root.path("errorMessage").path("code").asText() :
                    root.path("code").asText();

            String responseMessage = root.has("errorMessage") ?
                    root.path("errorMessage").path("message").asText() :
                    root.path("message").asText();

            if (!"INFO-000".equals(responseCode)) {
                log.warn("[실시간 도착정보] API 응답 오류 - code: {}, message: {}", responseCode, responseMessage);
                throw new TrainException(responseMessage);
            }
            String subwayIdFilter = getSubwayIdFromLineCode(decodedLineCode);

            List<ArrivalDto> arrivals = new ArrayList<>();
            for (JsonNode item : root.path("realtimeArrivalList")) {
                if (!subwayIdFilter.equals(item.path("subwayId").asText())) continue;

                String trainNo = item.path("btrainNo").asText();
                String statnNm = item.path("statnNm").asText();
                String statnTnm = item.path("bstatnNm").asText();
                String recptnDt = item.path("recptnDt").asText();
                String trainLineNm = item.path("trainLineNm").asText();

                String direction = "0".equals(item.path("updnLine").asText()) ? "상행" : "하행";
                String expressType = item.path("btrainSttus").asText();
                String status = switch (item.path("arvlCd").asInt()) {
                    case 0 -> "진입";
                    case 1 -> "도착";
                    case 2 -> "출발";
                    case 3 -> "전역출발";
                    case 4 -> "전역진입";
                    case 5 -> "전역도착";
                    case 99 -> "운행중";
                    default -> "알수없음";
                };
                boolean isLastTrain = "1".equals(item.path("lstcarAt").asText());

                arrivals.add(new ArrivalDto(trainNo, direction, statnTnm, trainLineNm, expressType, status, isLastTrain));
            }

            return new ArrivalResponseDto("도착 정보 조회 성공", arrivals);

        } catch (IOException e) {
            return new ArrivalResponseDto("응답 파싱 오류", List.of());
        } catch (com.idle.rushcutter.exception.TrainException e) {
            throw e;
        } catch (Exception e) {
            log.error("[실시간 도착정보] 요청 실패", e);
            return new ArrivalResponseDto("API 요청 실패", List.of());
        }
    }

    private String getSubwayIdFromLineCode(String lineCode) {
        return switch (lineCode) {
            case "1호선" -> "1001";
            case "2호선" -> "1002";
            case "3호선" -> "1003";
            case "4호선" -> "1004";
            case "5호선" -> "1005";
            case "6호선" -> "1006";
            case "7호선" -> "1007";
            case "8호선" -> "1008";
            case "9호선" -> "1009";
            case "경의중앙선" -> "1063";
            case "공항철도" -> "1065";
            case "경춘선" -> "1067";
            case "수인분당선" -> "1075";
            case "신분당선" -> "1077";
            case "우이신설선" -> "1092";
            case "서해선" -> "1093";
            case "경강선" -> "1081";
            case "GTX-A" -> "1032";
            default -> null;
        };
    }
}
