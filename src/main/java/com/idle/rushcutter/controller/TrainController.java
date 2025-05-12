package com.idle.rushcutter.controller;

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
}
