package com.idle.rushcutter.controller;

import com.idle.rushcutter.dto.congestion.CongestionResponseDto;
import com.idle.rushcutter.service.CongestionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/congestion/")
@RequiredArgsConstructor
public class CongestionController {

    private final CongestionService congestionService;

    @PostMapping("predict/{stationCode}")
    @Operation(summary = "객차별 예측 혼잡도 조회", description = "특정 지하철역에 대해 지정 시각 기준 열차 칸별 혼잡도 예측 정보를 제공합니다.")
    public ResponseEntity<?> getRealTimeCarCongestion(
            @PathVariable("stationCode") int stationCode,
            @RequestParam("line") int line,
            @RequestParam("updnLine") String updnLine,
            @RequestParam("hour") int hour,
            @RequestParam("minute") int minute,
            @RequestParam("dayType") String dayType,
            @RequestParam("month") int month
    ) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dateTime = now.withMonth(month).withHour(hour).withMinute(minute).withSecond(0).withNano(0);

        CongestionResponseDto response = congestionService.getPredictedCongestion(
                stationCode, line, updnLine, dateTime, dayType
        );
        return ResponseEntity.ok().body(
                Map.of(
                        "message", "혼잡도 예측을 성공했습니다.",
                        "result", response
                )
        );
    }
}

// TODO: CSV 파일 오류 해결하고 엔드포인트 정리해서 같이 커밋하기