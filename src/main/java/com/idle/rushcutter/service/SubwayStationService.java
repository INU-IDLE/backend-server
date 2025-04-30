package com.idle.rushcutter.service;

import com.idle.rushcutter.dto.station.TimetableResponseDto;
import com.idle.rushcutter.entity.SubwayStation;
import com.idle.rushcutter.exception.StationException;
import com.idle.rushcutter.repository.SubwayStationRepository;
import com.idle.rushcutter.util.OdsayApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubwayStationService {

    private final OdsayApiClient odsayApiClient;
    private final SubwayStationRepository stationRepository;

    public TimetableResponseDto getTimetable(String stationNumber, String lineCode) {
         SubwayStation station = stationRepository.findByNumberAndLineCode(stationNumber, lineCode)
                 .orElseThrow(() -> new StationException("stationNumber와 lineCode에 해당하는 역을 찾을 수 없습니다."));

        if (station.getOdsayStationId() == null) {
            throw new StationException("해당 역은 ODsay와 연결된 stationId가 없습니다.");
        }
        return odsayApiClient.getTimetable(station.getOdsayStationId(), station.getName(), lineCode);
    }

}
