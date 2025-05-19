package com.idle.rushcutter.dto.train;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;

public record ArrivalDto(
        String trainNo,
        String direction,
        String destination,
        String destinationWithDirection,
        String trainType,
        String arrivalTime,
        String trainPosition,
        String status,
        boolean isLastTrain
) {
    public static ArrivalDto from(JsonNode node) {
        return new ArrivalDto(
                node.path("btrainNo").asText(),
                mapUpdnLine(node.path("updnLine").asText()),
                node.path("bstatnNm").asText(),
                node.path("arvlMsg3").asText(),
                node.path("btrainSttus").asText(),
                node.path("arrivalTime").asText(),
                node.path("trainPositon").asText(),
                mapArrivalCode(node.path("arvlCd").asText()),
                "1".equals(node.path("lstcarAt").asText())
        );
    }

    public static List<ArrivalDto> listFrom(JsonNode root) {
        List<ArrivalDto> result = new ArrayList<>();
        if (root.has("realtimeArrivalList")) {
            for (JsonNode node : root.path("realtimeArrivalList")) {
                result.add(from(node));
            }
        }
        return result;
    }

    private static String mapUpdnLine(String code) {
        return switch (code) {
            case "0" -> "상행/내선";
            case "1" -> "하행/외선";
            default -> "알 수 없음";
        };
    }

    private static String mapArrivalCode(String code) {
        return switch (code) {
            case "0" -> "진입";
            case "1" -> "도착";
            case "2" -> "출발";
            case "3" -> "전역출발";
            case "4" -> "전역진입";
            case "5" -> "전역도착";
            case "99" -> "운행중";
            default -> "알 수 없음";
        };
    }
}
