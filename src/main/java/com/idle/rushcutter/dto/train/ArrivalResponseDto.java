package com.idle.rushcutter.dto.train;

import java.util.List;

public record ArrivalResponseDto(
        String stationName,
        List<ArrivalDto> arrivals
) {
}
