package com.idle.rushcutter.dto.congestion;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CongestionRequestDto {
    private int stationCode;
    private int line;
    private String updnLine;
    private LocalDateTime datetime;
    private String dayType;

    // DTO는 이미 완성된 상태로 보이며, 생성자와 toString이 필요하면 아래와 같이 추가할 수 있습니다.
    @Override
    public String toString() {
        return "CongestionRequestDto{" +
                "stationCode=" + stationCode +
                ", line=" + line +
                ", updnLine='" + updnLine + '\'' +
                ", datetime=" + datetime +
                ", dayType='" + dayType + '\'' +
                '}';
    }
}
