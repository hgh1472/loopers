package com.loopers.domain.metrics;

import java.time.LocalDate;

public class MetricCommand {
    public record IncrementLikes(
            Long productId,
            Long count,
            LocalDate createdAt
    ) {
    }

    public record DecrementLikes(
            Long productId,
            Long count,
            LocalDate createdAt
    ) {
    }

    public record IncrementSales(
            Long productId,
            Long quantity,
            LocalDate createdAt
    ) {
    }

    public record IncrementViews(
            Long productId,
            Long count,
            LocalDate createdAt
    ) {
    }
}
