package com.idle.rushcutter.initializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idle.rushcutter.entity.SubwayLine;
import com.idle.rushcutter.entity.SubwayStation;
import com.idle.rushcutter.entity.SubwayStationEdge;
import com.idle.rushcutter.repository.SubwayLineRepository;
import com.idle.rushcutter.repository.SubwayStationEdgeRepository;
import com.idle.rushcutter.repository.SubwayStationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubwayEdgeInitializer {

    private final ObjectMapper objectMapper;
    private final SubwayStationRepository stationRepository;
    private final SubwayLineRepository lineRepository;
    private final SubwayStationEdgeRepository edgeRepository;

    public void initialize() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            Resource[] resources = resolver.getResources("classpath:data/stations_*.json");

            for (Resource resource : resources) {
                try (InputStream is = resource.getInputStream()) {
                    JsonNode root = objectMapper.readTree(is);
                    JsonNode body = root.get("body");

                    List<JsonNode> sortedStations = new ArrayList<>();
                    body.forEach(sortedStations::add);
                    sortedStations.sort(Comparator.comparingInt(n -> n.get("stinConsOrdr").asInt())); // 순서 기준 정렬

                    String lineName = sortedStations.get(0).get("routNm").asText();
                    SubwayLine line = lineRepository.findByName(lineName).orElse(null);
                    if (line == null) continue;

                    List<JsonNode> mainLineStations = sortedStations.stream()
                            .filter(n -> !isBranchLine(n.get("stinCd").asText()))
                            .sorted(Comparator.comparingInt(n -> n.get("stinConsOrdr").asInt()))
                            .toList();

                    List<JsonNode> branchLineStations = sortedStations.stream()
                            .filter(n -> isBranchLine(n.get("stinCd").asText()))
                            .sorted(Comparator.comparingInt(n -> n.get("stinConsOrdr").asInt()))
                            .toList();

                    createEdgesForSortedStations(mainLineStations, lineName, line);
                    createEdgesForSortedStations(branchLineStations, lineName, line);
                    addLineSpecificExceptions(line);

                    log.info("[EDGE INITIALIZER] {} 완료", resource.getFilename());
                }
            }
        } catch (Exception e) {
            log.error("Edge 초기화 실패", e);
        }
    }

    private void addLineSpecificExceptions(SubwayLine line) {
        if ("1호선".equals(line.getName())) {
            connectIfPresent("141", "P142", line);
            connectIfPresent("P144", "P145", line);
            connectIfPresent("P157", "P158", line);
            connectIfPresent("100-3", "100-2", line);
            connectIfPresent("100-2", "100-1", line);
            connectIfPresent("100-1", "100", line);
        }

        if ("2호선".equals(line.getName())) {
            connectIfPresent("201", "202", line);
            connectIfPresent("234", "234-1", line);
            connectIfPresent("211", "211-1", line);
        }

        if ("5호선".equals(line.getName())) {
            connectIfPresent("548", "P549", line);
        }

        if ("6호선".equals(line.getName())) {
            edgeRepository.deleteByFromStationNumberAndToStationNumber("611", "610");
            edgeRepository.deleteByFromStationNumberAndToStationNumber("612", "611");
            edgeRepository.deleteByFromStationNumberAndToStationNumber("613", "612");
            edgeRepository.deleteByFromStationNumberAndToStationNumber("614", "613");
            edgeRepository.deleteByFromStationNumberAndToStationNumber("615", "614");
            edgeRepository.deleteByFromStationNumberAndToStationNumber("615", "616");
            edgeRepository.deleteByFromStationNumberAndToStationNumber("616", "615");

            connectIfPresent("610", "616", line);
            Optional<SubwayStation> from615 = stationRepository.findByNumber("615");
            Optional<SubwayStation> to610 = stationRepository.findByNumber("610");
            if (from615.isPresent() && to610.isPresent()) {
                edgeRepository.save(SubwayStationEdge.builder()
                        .line(line)
                        .fromStation(from615.get())
                        .toStation(to610.get())
                        .isExpress(false)
                        .build());
            }
        }

        if ("경의중앙".equals(line.getName())) {
            connectIfPresent("K315", "P314", line);
        }

        if ("경춘".equals(line.getName())) {
            connectIfPresent("K120", "K121", line);
            connectIfPresent("K121", "P122", line);
        }

        if ("GTX-A".equals(line.getName())) {
            // X106 - X108 구간 미개통
            edgeRepository.deleteByFromStationNumberAndToStationNumber("X106", "X108");
            edgeRepository.deleteByFromStationNumberAndToStationNumber("X108", "X106");
        }
    }

    private void connectIfPresent(String fromCode, String toCode, SubwayLine line) {
        log.debug("Trying to find station by number and lineCode: fromCode={}, lineCode={}, toCode={}, lineCode={}", fromCode, line.getLineCode(), toCode, line.getLineCode());
        Optional<SubwayStation> fromOpt = stationRepository.findByNumberAndLineCode(fromCode, line.getLineCode());
        Optional<SubwayStation> toOpt = stationRepository.findByNumberAndLineCode(toCode, line.getLineCode());
        log.debug("Result of findByNumberAndLineCode - fromOpt: {}, toOpt: {}", fromOpt, toOpt);

        if (fromOpt.isPresent() && toOpt.isPresent()) {
            edgeRepository.save(SubwayStationEdge.builder()
                    .line(line)
                    .fromStation(fromOpt.get())
                    .toStation(toOpt.get())
                    .isExpress(false)
                    .build());
            edgeRepository.save(SubwayStationEdge.builder()
                    .line(line)
                    .fromStation(toOpt.get())
                    .toStation(fromOpt.get())
                    .isExpress(false)
                    .build());
        }
    }

    private void createEdgesForSortedStations(List<JsonNode> stations, String lineName, SubwayLine line) {
        for (int i = 0; i < stations.size() - 1; i++) {
            JsonNode current = stations.get(i);
            JsonNode next = stations.get(i + 1);

            String currentNum = current.get("stinCd").asText();
            String nextNum = next.get("stinCd").asText();

            log.debug("Attempting edge creation between currentNum={} and nextNum={}", currentNum, nextNum);

            if (!isValidSequentialConnection(current, next)) continue;

            log.debug("Finding stations by number and lineCode: currentNum={}, lineCode={}, nextNum={}, lineCode={}", currentNum, line.getLineCode(), nextNum, line.getLineCode());
            Optional<SubwayStation> fromOpt = stationRepository.findByNumberAndLineCode(currentNum, line.getLineCode());
            Optional<SubwayStation> toOpt = stationRepository.findByNumberAndLineCode(nextNum, line.getLineCode());

            log.debug("Result of findByNumberAndLineCode - from: {}, to: {}", fromOpt, toOpt);

            if (fromOpt.isEmpty() || toOpt.isEmpty()) continue;

            SubwayStation from = fromOpt.get();
            SubwayStation to = toOpt.get();

            edgeRepository.save(SubwayStationEdge.builder()
                    .line(line)
                    .fromStation(from)
                    .toStation(to)
                    .isExpress(false)
                    .build());

            edgeRepository.save(SubwayStationEdge.builder()
                    .line(line)
                    .fromStation(to)
                    .toStation(from)
                    .isExpress(false)
                    .build());
        }
    }

    private boolean isValidSequentialConnection(JsonNode current, JsonNode next) {
        int currentSeq = current.get("stinConsOrdr").asInt();
        int nextSeq = next.get("stinConsOrdr").asInt();

        // stinConsOrdr가 동일한 경우는 연결 안 함
        if (currentSeq == nextSeq) return false;

        // 두 번호가 모두 지선(P 포함), 혹은 본선(P 미포함)이면서 1차이 나는 경우 허용
        if (Math.abs(currentSeq - nextSeq) == 1) return true;

        return false;
    }

    private boolean isBranchLine(String stinCd) {
        return stinCd.contains("P") || stinCd.matches(".*-\\d+");
    }
}
