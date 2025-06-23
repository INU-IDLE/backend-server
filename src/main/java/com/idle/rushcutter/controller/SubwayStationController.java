package com.idle.rushcutter.controller;

import com.idle.rushcutter.dto.station.TimetableResponseDto;
import com.idle.rushcutter.service.SubwayStationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/stations")
@RequiredArgsConstructor
@Tag(name = "Station API", description = "역 관련 API")
public class SubwayStationController {

    private final SubwayStationService subwayStationService;

    @GetMapping("/{stationNumber}/timetable")
    @Operation(summary = "역 시간표 조회", description = "역의 시간표를 조회합니다. (상/하행, 일반/급행/특급, 평일/토요일/공휴일)")
    public ResponseEntity<Map<String, Object>> getTimetable(
            @PathVariable String stationNumber,
            @RequestParam String lineCode
    ) {
        TimetableResponseDto response = subwayStationService.getTimetable(stationNumber, lineCode);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "시간표 조회에 성공했습니다.");
        result.put("result", response);
        return ResponseEntity.ok(result);
    }
}
