package com.loopers.application.metrics;

import java.time.LocalDate;

public class MetricsApplicationEvent {
    public record Updated(
            Long productId,
            Long count,
            Type type,
            LocalDate createdAt
    ) {
    }

    public enum Type {
        LIKE,
        VIEW,
        SALES
    }
}
