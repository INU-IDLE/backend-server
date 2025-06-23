package com.idle.rushcutter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExpressType {
    LOCAL(0, "일반"),
    RAPID(1, "급행"),
    EXPRESS(7, "특급");

    private final int code;
    private final String description;

    public static ExpressType fromCode(int code) {
        for (ExpressType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return LOCAL; // 기본값
    }
}