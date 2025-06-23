package com.idle.rushcutter.dto.train;

import java.util.List;

public record TrainPositionResponseDto(
        String message,
        List<TrainPositionDto> result
) {}