package com.idle.rushcutter.dto.congestion;

import java.util.Map;

public record RealTimeCongestionResponseDto(
        String subwayLine,
        String trainY,
        int congestionType,
        Map<String, CongestionInfo> cars
) {}
