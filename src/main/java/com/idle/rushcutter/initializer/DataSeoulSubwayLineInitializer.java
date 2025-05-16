package com.idle.rushcutter.initializer;

import com.idle.rushcutter.repository.SubwayLineRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataSeoulSubwayLineInitializer {

    private final SubwayLineRepository subwayLineRepository;

    @PostConstruct
    public void initializeSeoulLineIds() {
        Map<String, String> seoulLineIdMap = new java.util.HashMap<>();
        seoulLineIdMap.put("UI", "1092"); // 우이신설
        seoulLineIdMap.put("A1", "1065"); // 공항
        seoulLineIdMap.put("K1", "1075"); // 수인분당
        seoulLineIdMap.put("A", "1032"); // GTX-A
        seoulLineIdMap.put("K5", "1081"); // 경강
        seoulLineIdMap.put("3", "1003"); // 3호선
        seoulLineIdMap.put("5", "1005"); // 5호선
        seoulLineIdMap.put("2", "1002"); // 2호선
        seoulLineIdMap.put("U1", null); // 의정부 (ID unknown)
        seoulLineIdMap.put("6", "1006"); // 6호선
        seoulLineIdMap.put("D1", "1077"); // 신분당
        seoulLineIdMap.put("I1", null); // 인천1호선 (ID unknown)
        seoulLineIdMap.put("I2", null); // 인천2호선 (ID unknown)
        seoulLineIdMap.put("9", "1009"); // 9호선
        seoulLineIdMap.put("L1", null); // 신림선 (ID unknown)
        seoulLineIdMap.put("8", "1008"); // 8호선
        seoulLineIdMap.put("1", "1001"); // 1호선
        seoulLineIdMap.put("WS", "1093"); // 서해선
        seoulLineIdMap.put("7", "1007"); // 7호선
        seoulLineIdMap.put("4", "1004"); // 4호선
        seoulLineIdMap.put("K4", "1063"); // 경의중앙
        seoulLineIdMap.put("K2", "1067"); // 경춘
        seoulLineIdMap.put("G1", null); // 김포골드라인 (ID unknown)
        seoulLineIdMap.put("E1", null); // 에버라인 (ID unknown)

        subwayLineRepository.findAll().forEach(line -> {
            String seoulId = seoulLineIdMap.get(line.getLineCode());
            if (seoulId != null) {
                line.setSeoulLineId(seoulId);
                subwayLineRepository.save(line);
            }
        });
    }
}
