package com.idle.rushcutter.initializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idle.rushcutter.entity.SubwayStation;
import com.idle.rushcutter.repository.SubwayStationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ODsayStationMappingInitializer {

    private final SubwayStationRepository stationRepository;
    private final ObjectMapper objectMapper;

    public void initialize() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource resource = resolver.getResource("classpath:mapping/odsay_station_mapping.json");

            try (InputStream input = resource.getInputStream()) {
                JsonNode root = objectMapper.readTree(input);

                Iterator<String> lineNames = root.fieldNames();
                while (lineNames.hasNext()) {
                    String lineName = lineNames.next();
                    JsonNode lineMappings = root.get(lineName);
                    for (JsonNode node : lineMappings) {
                        String internalCode = node.get("internalCode").asText();
                        String externalCode = node.get("externalCode").asText();

                        Optional<SubwayStation> stationOpt;
                        if (internalCode.equals("119")) {
                            if (lineName.equals("경춘")) {
                                stationOpt = stationRepository.findByNumberAndLineCode("119", "K2");
                            } else {
                                stationOpt = stationRepository.findByNumberAndLineCode("119", "1");
                            }
                        } else if (internalCode.startsWith("K1") &&
                                (lineName.equals("경의중앙") || lineName.equals("경춘"))) {
                            String lineCode = resolveLineCode(lineName);
                            stationOpt = stationRepository.findByNumberAndLineCode(internalCode, lineCode);
                        } else {
                            stationOpt = stationRepository.findByNumber(internalCode);
                        }

                        if (stationOpt.isPresent()) {
                            SubwayStation station = stationOpt.get();
                            station.setOdsayStationId(externalCode);
                            stationRepository.save(station);
                            log.info("[수동 매핑] {}({}) -> {}", internalCode, lineName, externalCode);
                        } else {
                            log.warn("[수동 매핑 실패] 역을 찾을 수 없음: {}({})", internalCode, lineName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("[수동 매핑 오류] OdsayStationMappingInitializer 중 오류 발생", e);
        }
    }

    private String resolveLineCode(String lineName) {
        return switch (lineName) {
            case "경의중앙" -> "K4";
            case "경춘" -> "K2";
            default -> "";
        };
    }
}