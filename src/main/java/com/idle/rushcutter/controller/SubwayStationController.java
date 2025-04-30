package com.idle.rushcutter.controller;

import com.idle.rushcutter.dto.station.TimetableResponseDto;
import com.idle.rushcutter.service.SubwayStationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stations")
@RequiredArgsConstructor
@Tag(name = "Station API", description = "역 관련 API")
public class SubwayStationController {

    private final SubwayStationService subwayStationService;

    @GetMapping("/{stationNumber}/timetable")
    @Operation(summary = "역 시간표 조회", description = "역의 시간표를 조회합니다. (상/하행, 일반/급행/특급, 평일/토요일/공휴일)")
    public ResponseEntity<java.util.Map<String, Object>> getTimetable(
            @PathVariable String stationNumber,
            @RequestParam String lineCode
    ) {
        TimetableResponseDto response = subwayStationService.getTimetable(stationNumber, lineCode);
        return ResponseEntity.ok(java.util.Map.of(
                "message", "시간표 조회를 성공했습니다.",
                "result", response
        ));
    }
}
