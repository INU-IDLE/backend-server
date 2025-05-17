package com.idle.rushcutter.util;

public class ODsayLineNameNormalizer {
    public static String normalize(String laneName, String startId) {
        try {
            int id = Integer.parseInt(startId);
            if ("1호선".equals(laneName) && id >= 20110 && id <= 20139) {
                return "인천1호선";
            }
            if ("2호선".equals(laneName) && id >= 20210 && id <= 20236) {
                return "인천2호선";
            }
        } catch (NumberFormatException e) {
            // Ignore malformed IDs
        }
        return laneName;
    }
}
