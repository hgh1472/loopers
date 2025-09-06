package com.loopers.domain.metrics;

import java.time.LocalDate;
import java.util.List;

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
            List<SaleLine> saleLines,
            LocalDate createdAt
    ) {
    }

    public record SaleLine(
            Long productId,
            Long quantity
    ) {
    }

    public record IncrementView(
            Long productId,
            LocalDate createdAt
    ) {
    }
}
