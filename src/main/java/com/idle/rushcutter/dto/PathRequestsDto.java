package com.idle.rushcutter.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PathRequestsDto {
    private String startStationId;
    private String endStationId;

    @Getter
    @Setter
    public static class ExchangeInfo {
        private String laneName;
        private String startName;
        private String exName;
        private int exSID;
        private String fastTrainCar;
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

    private int globalStationCount;
    private int cashFare;
    private List<ExchangeInfo> exchanges;
    private List<StationInfo> stations;
}
