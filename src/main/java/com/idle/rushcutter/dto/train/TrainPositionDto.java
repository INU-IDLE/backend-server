package com.idle.rushcutter.dto.train;

public record TrainPositionDto(
        String trainNo,
        String currentStation,
        String direction,     // "상행", "하행"
        String status,        // "진입", "도착", "출발", "전역출발"
        String expressType,   // "일반", "급행", "특급"
        String destination,   // 예: "신창 방면"
        boolean isLastTrain,  // true: 막차
        String updatedAt,     // 예: "2025-05-07 15:44:13"
        int rowNum,           // 열차 순번
        int totalCount        // 전체 열차 수
) {}