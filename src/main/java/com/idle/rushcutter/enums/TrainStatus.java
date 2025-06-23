package com.idle.rushcutter.enums;

public enum TrainStatus {
    ENTERING(0, "진입"),
    ARRIVING(1, "도착"),
    DEPARTING(2, "출발"),
    PREVIOUS_STATION_DEPARTED(3, "전역 출발");

    private final int code;
    private final String description;

    TrainStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String of(int code) {
        for (TrainStatus status : values()) {
            if (status.code == code) return status.description;
        }
        return "알 수 없음";
    }
}
