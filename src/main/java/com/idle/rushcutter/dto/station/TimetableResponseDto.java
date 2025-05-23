package com.idle.rushcutter.dto.station;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class TimetableResponseDto {
    private String stationName;
    private String lineCode;
    private String prevStationName;
    private String nextStationName;
    private TimetableDayScheduleDto weekdaySchedule;
    private TimetableDayScheduleDto saturdaySchedule;
    private TimetableDayScheduleDto holidaySchedule;
}
