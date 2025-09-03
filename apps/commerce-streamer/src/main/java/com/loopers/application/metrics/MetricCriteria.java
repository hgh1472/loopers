package com.loopers.application.metrics;

import java.time.ZonedDateTime;

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
            Long productId,
            Long quantity,
            ZonedDateTime createdAt
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
