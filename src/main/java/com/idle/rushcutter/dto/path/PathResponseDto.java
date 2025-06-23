package com.idle.rushcutter.dto.path;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PathResponseDto {
    private String globalStartName;
    private String globalEndName;
    private int globalTravelTime;
    private int fare;
    private int globalStationCount;
    private int cashFare;
    private List<DriveInfo> route;
    private List<ExchangeInfo> exchanges;
    private List<StationInfo> stations;

    @Getter
    @Setter
    public static class DriveInfo {
        private String laneName;
        private String startName;
        private int stationCount;
        private String wayName;
        private String direction;
    }

    @Getter
    @Setter
    public static class ExchangeInfo {
        private String laneName;
        private String startName;
        private String exName;
        private int exSID;
        private String fastTrainCar;
        private int exWalkTime;
    }

    @Getter
    @Setter
    public static class StationInfo {
        private int startID;
        private String startName;
        private int endSID;
        private String endName;
        private int travelTime;
        private boolean isTransferStation;
    }
}
