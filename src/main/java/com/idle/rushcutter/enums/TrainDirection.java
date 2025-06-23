package com.idle.rushcutter.enums;

public enum TrainDirection {
    UP(0, "상행"),
    DOWN(1, "하행");

    private final int code;
    private final String description;

    TrainDirection(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String of(int code) {
        for (TrainDirection dir : values()) {
            if (dir.code == code) return dir.description;
        }
        return "알 수 없음";
    }
}
