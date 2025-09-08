package com.loopers.domain.metrics;

import java.time.LocalDate;
import java.util.List;

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
