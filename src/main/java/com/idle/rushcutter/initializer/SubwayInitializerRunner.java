package com.idle.rushcutter.initializer;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubwayInitializerRunner {

    private final SubwayStationLineInitializer stationLineInitializer;
    private final SubwayEdgeInitializer edgeInitializer;

    @PostConstruct
    public void runInitializers() {
        log.info("[INITIALIZER] SubwayStationLineInitializer 시작");
        stationLineInitializer.initialize();
        log.info("[INITIALIZER] SubwayStationLineInitializer 완료");

        log.info("[INITIALIZER] SubwayEdgeInitializer 시작");
        edgeInitializer.initialize();
        log.info("[INITIALIZER] SubwayEdgeInitializer 완료");
    }
}
