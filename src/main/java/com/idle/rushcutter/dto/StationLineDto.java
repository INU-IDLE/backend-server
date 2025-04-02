package com.idle.rushcutter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "역-노선 연결 정보 DTO")
public class StationLineDto {

    @Schema(description = "역 번호", example = "201")
    private String stationNumber;

    @Schema(description = "역 이름", example = "시청")
    private String stationName;

    @Schema(description = "노선 이름", example = "2호선")
    private String lineName;

    @Schema(description = "해당 노선 내 정렬 순서", example = "5")
    private int sequence;

    @Schema(description = "급행 여부", example = "false")
    private Boolean isExpress;
}
