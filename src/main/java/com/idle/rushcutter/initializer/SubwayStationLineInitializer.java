package com.idle.rushcutter.initializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idle.rushcutter.entity.SubwayLine;
import com.idle.rushcutter.entity.SubwayStation;
import com.idle.rushcutter.entity.SubwayStationLine;
import com.idle.rushcutter.repository.SubwayLineRepository;
import com.idle.rushcutter.repository.SubwayStationLineRepository;
import com.idle.rushcutter.repository.SubwayStationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubwayStationLineInitializer {

    private final SubwayStationRepository stationRepository;
    private final SubwayLineRepository lineRepository;
    private final SubwayStationLineRepository stationLineRepository;
    private final ObjectMapper objectMapper;

    public void initialize() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:data/stations_*.json");

            for (Resource resource : resources) {
                try (InputStream is = resource.getInputStream()) {
                    JsonNode root = objectMapper.readTree(is);
                    JsonNode body = root.get("body");

                    int inserted = 0;
                    List<JsonNode> sortedStations = new ArrayList<>();
                    body.forEach(sortedStations::add);

                    sortedStations.sort(Comparator.comparing(n -> n.get("stinCd").asText()));
                    int sequence = 1;

                    for (JsonNode stationNode : sortedStations) {
                        String number = stationNode.get("stinCd").asText();
                        String lnCd = stationNode.get("lnCd").asText();
                        String name = stationNode.get("stinNm").asText();
                        String lineName = stationNode.get("routNm").asText();

                        SubwayStation station = stationRepository.findByNumber(number)
                                .orElseGet(() -> stationRepository.save(
                                        SubwayStation.builder()
                                                .number(number)
                                                .lineCode(lnCd)
                                                .name(name)
                                                .odsayStationId(number)
                                                .transferAvailable(true)
                                                .build()
                                ));

                        SubwayLine line = lineRepository.findByName(lineName)
                                .orElseGet(() -> lineRepository.save(
                                        SubwayLine.builder()
                                                .name(lineName)
                                                .lineCode(lnCd)
                                                .build()
                                ));

                        SubwayStationLine stationLine = SubwayStationLine.builder()
                                .station(station)
                                .line(line)
                                .sequence(sequence++)
                                .isExpress(false)
                                .build();

                        stationLineRepository.save(stationLine);
                        inserted++;
                    }
                }
            }
        } catch (Exception e) {
            log.error("SubwayStationLine 초기화 중 오류 발생", e);
        }
    }
}
