package com.idle.rushcutter.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idle.rushcutter.dto.station.TimetableEntryDto;
import com.idle.rushcutter.dto.station.TimetableResponseDto;
import com.idle.rushcutter.exception.PathException;
import com.idle.rushcutter.exception.StationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class OdsayApiClient {

    @Value("${odsay-api-key}")
    private String apiKey;

    public JsonNode getSubwayPath(String startId, String endId, String sopt) {
        try {
            String url = String.format("https://api.odsay.com/v1/api/subwayPath?" + "apiKey=%s&CID=1000&SID=%s&EID=%s&lang=0&output=json", URLEncoder.encode(apiKey, StandardCharsets.UTF_8), URLEncoder.encode(startId, StandardCharsets.UTF_8), URLEncoder.encode(endId, StandardCharsets.UTF_8));
            if (sopt != null && !sopt.isEmpty()) {
                url += "&Sopt=" + URLEncoder.encode(sopt, StandardCharsets.UTF_8);
            }
            URI uri = new URI(url);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            if (root.get("result") == null) {
                throw new PathException(root);
            }

            return root.get("result");
        } catch (Exception e) {
            throw new PathException("[ODSay API]: " + e.getMessage(), e);
        }
    }

    public TimetableResponseDto getTimetable(String odsayStationId, String stationName, String lineCode) {
        try {
            List<TimetableEntryDto> up = fetchTimetable(odsayStationId, 1);
            List<TimetableEntryDto> down = fetchTimetable(odsayStationId, 2);

            return TimetableResponseDto.builder()
                    .stationName(stationName)
                    .lineCode(lineCode)
                    .up(up)
                    .down(down)
                    .build();
        } catch (Exception e) {
            throw new StationException("ODsay API 시간표 요청 실패: " + e.getMessage(), e);
        }
    }

    private List<TimetableEntryDto> fetchTimetable(String stationId, int wayCode) {
        try {
            String url = String.format(
                "https://api.odsay.com/v1/api/searchSubwaySchedule?apiKey=%s&stationID=%s&wayCode=%d&lang=0&showExpressTime=1&sepExpressTime=1",
                URLEncoder.encode(apiKey, StandardCharsets.UTF_8),
                URLEncoder.encode(stationId, StandardCharsets.UTF_8),
                wayCode
            );

            URI uri = new URI(url);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            if (!root.has("result")) {
                throw new StationException("ODsay API result 필드 없음");
            }

            String scheduleKey = resolveScheduleKey();
            String directionKey = (wayCode == 1) ? "up" : "down";
            JsonNode timeList = root.path("result").path(scheduleKey).path(directionKey);

            List<TimetableEntryDto> result = new ArrayList<>();
            for (JsonNode item : timeList) {
                int subwayClass = item.path("subwayClass").asInt();
                String trainType = switch (subwayClass) {
                    case 1 -> "EXPRESS";
                    case 2 -> "SPECIAL";
                    default -> "NORMAL";
                };
                result.add(TimetableEntryDto.builder()
                        .departureTime(item.path("departureTime").asText())
                        .startStationName(item.path("startStationName").asText())
                        .endStationName(item.path("endStationName").asText())
                        .trainType(trainType)
                        .isFirstTrain(item.path("firstLastFlag").asInt() == 1)
                        .build());
            }

            return result;
        } catch (Exception e) {
            throw new StationException("ODsay 시간표 fetch 실패: " + e.getMessage(), e);
        }
    }

    private String resolveScheduleKey() {
        return switch (LocalDate.now().getDayOfWeek()) {
            case SATURDAY -> "saturdaySchedule";
            case SUNDAY -> "holidaySchedule";
            default -> "weekdaySchedule";
        };
    }
}
