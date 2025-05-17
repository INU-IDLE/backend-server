package com.idle.rushcutter.dto.congestion;

import com.idle.rushcutter.enums.CongestionLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CongestionInfo {
    private CongestionLevel level;
    private double percentage;
}
