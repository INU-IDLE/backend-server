package com.idle.rushcutter.controller;

import com.idle.rushcutter.dto.path.PathResponseDto;
import com.idle.rushcutter.service.PathService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/path")
@RequiredArgsConstructor
@Tag(name = "Path API", description = "지하철 경로 탐색 API")
public class PathController {

    private final PathService pathService;

    @GetMapping("/shortest")
    @Operation(summary = "최단 경로 탐색 (정적)", description = "출발역과 도착역 간의 최단 거리 경로를 탐색합니다.")
    public ResponseEntity<Map<String, Object>> getRecommendedPath(
            @RequestParam("start") String startId,
            @RequestParam("end") String endId
    ) {
        PathResponseDto path = pathService.findRecommendedPath(startId, endId, "1");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "최단 경로 탐색이 완료되었습니다.");
        result.put("result", path);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/min-transfer")
    @Operation(summary = "최소 환승 경로 탐색 (정적)", description = "출발역과 도착역 간의 최소 환승 경로를 탐색합니다.")
    public ResponseEntity<Map<String, Object>> getMinTransferPath(
            @RequestParam("start") String startId,
            @RequestParam("end") String endId
    ) {
        PathResponseDto path = pathService.findRecommendedPath(startId, endId, "2");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "최소 환승 경로 탐색이 완료되었습니다.");
        result.put("result", path);
        return ResponseEntity.ok(result);
    }
}
