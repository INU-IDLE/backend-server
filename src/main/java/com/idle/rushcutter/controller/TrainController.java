package com.idle.rushcutter.controller;

import com.idle.rushcutter.dto.train.ArrivalResponseDto;
import com.idle.rushcutter.dto.train.TrainPositionResponseDto;
import com.idle.rushcutter.service.TrainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lines")
@RequiredArgsConstructor
@Tag(name = "Train", description = "열차 위치 관련 API")
public class TrainController {

    private final TrainService trainService;

    @Operation(summary = "노선별 실시간 열차 위치 조회", description = "지정된 노선 코드에 해당하는 실시간 열차 위치정보를 조회합니다.")
    @GetMapping("/{lineCode}/trains/positions")
    public TrainPositionResponseDto getTrainPositions(@PathVariable String lineCode) {
        return trainService.getTrainPositions(lineCode);
    }

    @Operation(summary = "지하철역 실시간 도착 정보 조회", description = "지정된 노선명과 지하철역명에 해당하는 실시간 도착정보를 조회합니다." +
            "(지원 노선 1-9호선, 공항철도, 경의중앙선, 경춘선, 수인분당선, 신분당선, 경강선, 서해선, 우이신설선, GTX-A)")
    @GetMapping("/{lineName}/trains/{stationName}/arrivals")
    public ArrivalResponseDto getStationArrivals(@PathVariable String lineName, @PathVariable String stationName) {
        return trainService.getStationArrivals(lineName, stationName);
    }
}
