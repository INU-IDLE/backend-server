package com.idle.rushcutter.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idle.rushcutter.dto.station.TimetableEntryDto;
import com.idle.rushcutter.dto.station.TimetableResponseDto;
import com.idle.rushcutter.dto.station.TimetableDayScheduleDto;
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
            TimetableDayScheduleDto weekdaySchedule = TimetableDayScheduleDto.builder()
                .up(fetchTimetable(odsayStationId, 1, "weekdaySchedule"))
                .down(fetchTimetable(odsayStationId, 2, "weekdaySchedule"))
                .build();

            TimetableDayScheduleDto saturdaySchedule = TimetableDayScheduleDto.builder()
                .up(fetchTimetable(odsayStationId, 1, "saturdaySchedule"))
                .down(fetchTimetable(odsayStationId, 2, "saturdaySchedule"))
                .build();

            TimetableDayScheduleDto holidaySchedule = TimetableDayScheduleDto.builder()
                .up(fetchTimetable(odsayStationId, 1, "holidaySchedule"))
                .down(fetchTimetable(odsayStationId, 2, "holidaySchedule"))
                .build();

            // Fetch prev/next station names from ODsay API
            String url = String.format(
                "https://api.odsay.com/v1/api/subwayStationInfo?apiKey=%s&stationID=%s&lang=0",
                URLEncoder.encode(apiKey, StandardCharsets.UTF_8),
                URLEncoder.encode(odsayStationId, StandardCharsets.UTF_8)
            );
            URI uri = new URI(url);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode resultNode = root.path("result");
            JsonNode prevStation = resultNode.path("prevOBJ").path("station");
            JsonNode nextStation = resultNode.path("nextOBJ").path("station");

            String prevStationName = prevStation.isArray() && prevStation.size() > 0
                ? prevStation.get(0).path("stationName").asText() : null;
            String nextStationName = nextStation.isArray() && nextStation.size() > 0
                ? nextStation.get(0).path("stationName").asText() : null;

            return TimetableResponseDto.builder()
                    .stationName(stationName)
                    .lineCode(lineCode)
                    .prevStationName(prevStationName)
                    .nextStationName(nextStationName)
                    .weekdaySchedule(weekdaySchedule)
                    .saturdaySchedule(saturdaySchedule)
                    .holidaySchedule(holidaySchedule)
                    .build();
        } catch (Exception e) {
            throw new StationException("ODsay API 시간표 요청 실패: " + e.getMessage(), e);
        }
    }

    private List<TimetableEntryDto> fetchTimetable(String stationId, int wayCode, String scheduleKey) {
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

            // use provided scheduleKey
            String directionKey = (wayCode == 1) ? "up" : "down";
            JsonNode timeList = root.path("result").path(scheduleKey).path(directionKey);

            List<TimetableEntryDto> result = new ArrayList<>();
            for (JsonNode item : timeList) {
                int subwayClass = item.path("subwayClass").asInt();
                String trainType = switch (subwayClass) {
                    case 1 -> "RAPID";
                    case 2 -> "EXPRESS";
                    default -> "LOCAL";
                };
                result.add(TimetableEntryDto.builder()
                        .departureTime(item.path("departureTime").asText())
                        .startStationName(item.path("startStationName").asText())
                        .endStationName(item.path("endStationName").asText())
                        .trainType(trainType)
                        .isFirstTrain(item.path("firstLastFlag").asInt() == 1)
                        .isLastTrain(item.path("firstLastFlag").asInt() == 2)
                        .build());
            }

            return result;
        } catch (Exception e) {
            throw new StationException("ODsay 시간표 fetch 실패: " + e.getMessage(), e);
        }
    }
}
