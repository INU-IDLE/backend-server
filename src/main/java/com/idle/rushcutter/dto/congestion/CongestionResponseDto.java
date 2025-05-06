package com.idle.rushcutter.dto.congestion;

import com.idle.rushcutter.enums.CongestionLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CongestionResponseDto {
    private int station;
    private LocalDateTime dateTime;
    private String updnLine;
    private String dayType;
    private int line;
    private Map<String, CongestionLevel> predictions;
}
