package com.idle.rushcutter.controller;

import com.idle.rushcutter.dto.StationLineDto;
import com.idle.rushcutter.entity.SubwayStation;
import com.idle.rushcutter.entity.SubwayStationEdge;
import com.idle.rushcutter.repository.SubwayStationEdgeRepository;
import com.idle.rushcutter.repository.SubwayStationLineRepository;
import com.idle.rushcutter.repository.SubwayStationRepository;
import com.idle.rushcutter.repository.SubwayLineRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/debug")
public class SubwayDebugController {

    private final SubwayStationRepository stationRepository;
    private final SubwayStationLineRepository stationLineRepository;
    private final SubwayStationEdgeRepository edgeRepository;
    private final SubwayLineRepository lineRepository; // Added field

    @GetMapping("/station-lines")
    @Operation(summary = "역-노선 연결 정보 조회", description = "노선명을 기준으로 필터링된 역-노선 연결 정보를 조회합니다.")
    public List<StationLineDto> getStationLinesByLineName(
            @RequestParam(required = false) String lineName) {
        return stationLineRepository.findByLineName(lineName);
    }

    @GetMapping("/station-edges")
    @Operation(summary = "역 간 연결(Edge) 정보 조회", description = "노선명을 기준으로 필터링된 역 간 연결(Edge) 정보를 조회합니다.")
    public List<Map<String, Object>> getAllStationEdges(@RequestParam(required = false) String lineName) {
        List<SubwayStationEdge> edges;
        if (lineName != null) {
            var line = lineRepository.findByName(lineName)
                    .orElseThrow(() -> new IllegalArgumentException("Line not found: " + lineName));
            edges = edgeRepository.findByLineId(line.getId());
        } else {
            edges = edgeRepository.findAll();
        }

        return edges.stream().map(edge -> {
            Map<String, Object> map = new HashMap<>();
            map.put("edgeId", edge.getId());
            map.put("line", edge.getLine().getName());
            map.put("fromStation", edge.getFromStation().getName() + " (" + edge.getFromStation().getNumber() + ")");
            map.put("toStation", edge.getToStation().getName() + " (" + edge.getToStation().getNumber() + ")");
            map.put("isExpress", edge.getIsExpress());
            return map;
        }).collect(Collectors.toList());
    }

    @GetMapping("/{stationNumber}/neighbors")
    public List<String> getNeighbors(@PathVariable String stationNumber) {
        SubwayStation station = stationRepository.findByNumber(stationNumber)
                .orElseThrow(() -> new IllegalArgumentException("Station not found"));

        List<SubwayStationEdge> edges = edgeRepository.findByFromStation(station);
        return edges.stream()
                .map(edge -> edge.getToStation().getNumber() + " - " + edge.getToStation().getName())
                .collect(Collectors.toList());
    }
}
