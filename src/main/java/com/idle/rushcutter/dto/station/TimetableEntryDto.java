package com.idle.rushcutter.dto.station;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TimetableEntryDto {
    private String departureTime;
    private String startStationName;
    private String endStationName;
    private String trainType;
    private boolean isFirstTrain;
    private boolean isLastTrain;
}
