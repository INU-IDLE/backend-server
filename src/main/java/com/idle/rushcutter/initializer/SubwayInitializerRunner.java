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
    private final ODsayStationMappingInitializer odsayStationMappingInitializer;
    private final DataGovStationMappingInitializer dataGovStationMappingInitializer;
    private final DataSeoulSubwayLineInitializer dataSeoulSubwayLineInitializer;

    @PostConstruct
    public void runInitializers() {
        stationLineInitializer.initialize();
        log.info("[INITIALIZER] SubwayStationLineInitializer 완료");

        edgeInitializer.initialize();
        log.info("[INITIALIZER] SubwayEdgeInitializer 완료");

        odsayStationMappingInitializer.initialize();
        log.info("[INITIALIZER] ODsayStationMappingInitializer 완료");

        dataGovStationMappingInitializer.initialize();
        log.info("[INITIALIZER] DataGovStationMappingInitializer 완료");

        dataSeoulSubwayLineInitializer.initializeSeoulLineIds();
        log.info("[INITIALIZER] DataSeoulSubwayLineInitializer 완료");
    }
}
