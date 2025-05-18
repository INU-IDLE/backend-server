package com.idle.rushcutter.util;

import com.idle.rushcutter.dto.congestion.CongestionInfo;
import com.idle.rushcutter.enums.CongestionLevel;

import java.util.HashMap;
import java.util.Map;

public class CongestionParser {

    public static Map<String, CongestionInfo> parse(String congestionCarStr) {
        String[] percentages = congestionCarStr.split("\\|");
        Map<String, CongestionInfo> result = new HashMap<>();

        for (int i = 0; i < percentages.length; i++) {
            double percentage = Double.parseDouble(percentages[i]);
            CongestionLevel level = CongestionLevel.fromPercentage(percentage);
            result.put("car_" + (i + 1), new CongestionInfo(level, percentage));
        }
        return result;
    }
}
