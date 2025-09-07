package com.loopers.application.metrics;

import java.time.ZonedDateTime;
import java.util.List;

public class MetricCriteria {
    public record IncrementLike(
            String eventId,
            String consumerGroup,
            String payload,
            Long productId,
            ZonedDateTime createdAt
    ) {
    }

    public record DecrementLike(
            String eventId,
            String consumerGroup,
            String payload,
            Long productId,
            ZonedDateTime createdAt
    ) {
    }

    public record IncrementSales(
            String eventId,
            String consumerGroup,
            String payload,
            List<SaleLine> lines,
            ZonedDateTime createdAt
    ) {
    }

    public record SaleLine(
            Long productId,
            Long quantity
    ) {
    }

    public record IncrementView(
            String eventId,
            String consumerGroup,
            String payload,
            Long productId,
            ZonedDateTime createdAt
    ) {
    }
}
