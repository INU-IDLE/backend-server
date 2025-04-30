package com.idle.rushcutter.initializer;

import com.idle.rushcutter.entity.SubwayStation;
import com.idle.rushcutter.repository.SubwayStationRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataGovStationMappingInitializer {

    private final SubwayStationRepository stationRepository;

    public void initialize() {
        Path path = Paths.get("src/main/resources/mapping/서울교통공사_역명 지하철역 검색.csv");
        try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("EUC-KR"))) {
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                String dataGovCode = line[0];    // 전철역코드
                String stationName = line[1];    // 전철역명
                String lineCode = line[2];       // 수정된 코드값
                String number = line[3];         // 외부코드

                List<SubwayStation> stations = stationRepository.findAllByNumberAndLineCode(number, lineCode);
                for (SubwayStation station : stations) {
                    station.setDataGovStationId(dataGovCode);
                    stationRepository.save(station);
                    log.debug("Mapped [{} - {}] -> {}", number, lineCode, dataGovCode);
                }
            }
            log.info("[INITIALIZER] DataGovStationMappingInitializer: 역 코드 매핑 완료");
        } catch (Exception e) {
            log.error("DataGovStationMappingInitializer 실행 중 오류 발생", e);
        }
    }
}
