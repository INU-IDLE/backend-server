package com.idle.rushcutter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.idle.rushcutter.dto.path.PathResponseDto;
import com.idle.rushcutter.entity.SubwayStation;
import com.idle.rushcutter.exception.PathException;
import com.idle.rushcutter.repository.SubwayStationRepository;
import com.idle.rushcutter.util.ODsayLineNameNormalizer;
import com.idle.rushcutter.util.OdsayApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PathService {

    private final OdsayApiClient odsayApiClient;
    private final SubwayStationRepository stationRepository;

    public PathResponseDto findRecommendedPath(String startNumber, String endNumber, String option) {
        String startExternalId = stationRepository.findByNumber(startNumber)
                .map(SubwayStation::getOdsayStationId)
                .orElseThrow(() -> new PathException("출발역 정보를 찾을 수 없습니다."));
        String endExternalId = stationRepository.findByNumber(endNumber)
                .map(SubwayStation::getOdsayStationId)
                .orElseThrow(() -> new PathException("도착역 정보를 찾을 수 없습니다."));

        JsonNode result = odsayApiClient.getSubwayPath(startExternalId, endExternalId, option);

        if (result == null || !result.has("globalStartName")) { // Check for null or missing data
            throw new PathException("Invalid path data received from API.");
        }

        PathResponseDto dto = new PathResponseDto();
        dto.setGlobalStartName(result.get("globalStartName").asText());
        dto.setGlobalEndName(result.get("globalEndName").asText());
        dto.setGlobalTravelTime(result.get("globalTravelTime").asInt());
        dto.setFare(result.get("fare").asInt());
        dto.setGlobalStationCount(result.get("globalStationCount").asInt());
        dto.setCashFare(result.get("cashFare").asInt());

        List<PathResponseDto.DriveInfo> driveList = new ArrayList<>();
        for (JsonNode drive : result.get("driveInfoSet").get("driveInfo")) {
            PathResponseDto.DriveInfo d = new PathResponseDto.DriveInfo();
            String laneName = drive.get("laneName").asText();
            log.info("Raw laneName from ODsay: {}, for segment startName: {}", laneName, drive.get("startName").asText());
            String startName = drive.get("startName").asText();
            String startId = null;
            for (JsonNode station : result.get("stationSet").get("stations")) {
                if (station.get("startName").asText().equals(startName)) {
                    startId = station.get("startID").asText();
                    break;
                }
            }
            if (startId == null) {
                log.warn("Could not find startID for segment starting at {}", startName);
                startId = "0"; // fallback to avoid NumberFormatException
            }
            log.info("Using startID for normalization: {}", startId);
            String normalizedLaneName = ODsayLineNameNormalizer.normalize(laneName, startId);
            log.info("Normalized lane: {} → {}", laneName, normalizedLaneName);
            d.setLaneName(normalizedLaneName);
            if (drive.has("wayName") && !drive.get("wayName").isNull()) {
                d.setWayName(drive.get("wayName").asText());
                d.setDirection(com.idle.rushcutter.enums.DirectionResolver.resolve(normalizedLaneName, d.getWayName()));
                log.info("Resolved direction for {} ({}): {}", normalizedLaneName, d.getWayName(), d.getDirection());
            }
            d.setStartName(drive.get("startName").asText());
            d.setStationCount(drive.get("stationCount").asInt());
            driveList.add(d);
        }

        // ExchangeInfo parsing
        List<PathResponseDto.ExchangeInfo> exchangeList = new ArrayList<>();
        if (result.has("exChangeInfoSet")) {
            for (JsonNode ex : result.get("exChangeInfoSet").get("exChangeInfo")) {
                log.info("Exchange exSID (used for normalization): {}", ex.get("exSID").asInt());
                PathResponseDto.ExchangeInfo e = new PathResponseDto.ExchangeInfo();
                String rawLaneName = ex.get("laneName").asText();
                String exStartId = null;
                for (JsonNode station : result.get("stationSet").get("stations")) {
                    if (station.get("startName").asText().equals(ex.get("startName").asText())) {
                        exStartId = station.get("startID").asText();
                        break;
                    }
                }
                if (exStartId == null) {
                    exStartId = String.valueOf(ex.get("exSID").asInt());
                }
                log.info("Using exStartID for normalization: {}", exStartId);
                String normalized = ODsayLineNameNormalizer.normalize(rawLaneName, exStartId);
                log.info("Exchange normalized lane: {} → {}", rawLaneName, normalized);
                e.setLaneName(normalized);
                e.setStartName(ex.get("startName").asText());
                e.setExName(ex.get("exName").asText());
                e.setExSID(ex.get("exSID").asInt());
                int fastTrain = ex.get("fastTrain").asInt();
                int fastDoor = ex.get("fastDoor").asInt();
                e.setFastTrainCar(fastTrain + "-" + fastDoor);
                e.setExWalkTime(ex.get("exWalkTime").asInt());
                exchangeList.add(e);
            }
        }
        dto.setExchanges(exchangeList);

        // Extract transfer stations
        Set<String> transferStations = exchangeList.stream()
                .map(PathResponseDto.ExchangeInfo::getStartName)
                .collect(Collectors.toSet());

        // StationInfo parsing
        List<PathResponseDto.StationInfo> stationList = new ArrayList<>();
        for (JsonNode s : result.get("stationSet").get("stations")) {
            PathResponseDto.StationInfo si = new PathResponseDto.StationInfo();
            si.setStartID(s.get("startID").asInt());
            si.setStartName(s.get("startName").asText());
            si.setEndSID(s.get("endSID").asInt());
            si.setEndName(s.get("endName").asText());
            si.setTravelTime(s.get("travelTime").asInt());
            si.setTransferStation(transferStations.contains(si.getStartName()));
            stationList.add(si);
        }
        dto.setStations(stationList);

        dto.setRoute(driveList);
        return dto;
    }
}
