package com.loopers.key;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MetricsKeys {
    PRODUCT_SCORE("ranking:all:{date}");

    private final String key;

    public String getKey(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String keyDate = date.format(formatter);
        return key.replace("{date}", keyDate);
    }
}
