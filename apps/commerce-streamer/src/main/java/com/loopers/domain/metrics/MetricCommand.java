package com.loopers.domain.metrics;

import java.time.LocalDate;

public class MetricCommand {
    public record IncrementLike(
            Long productId,
            LocalDate createdAt
    ) {
    }

    public record DecrementLike(
            Long productId,
            LocalDate createdAt
    ) {
    }

    public record IncrementSales(
            Long productId,
            Long quantity,
            LocalDate createdAt
    ) {
    }

    public record IncrementView(
            Long productId,
            LocalDate createdAt
    ) {
    }
}
