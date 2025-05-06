package com.idle.rushcutter.enums;

public enum CongestionLevel {
    RELAXED("여유"),    // 0 ~ 80%
    NORMAL("보통"),     // 80 ~ 130%
    WARNING("주의"),    // 130 ~ 150%
    CROWDED("혼잡");    // 150% ~

    private final String label;

    CongestionLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static CongestionLevel fromPercentage(double percentage) {
        if (percentage <= 80) return RELAXED;
        else if (percentage <= 130) return NORMAL;
        else if (percentage <= 150) return WARNING;
        else return CROWDED;
    }
}
