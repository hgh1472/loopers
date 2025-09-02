package com.loopers.application.metrics;

public class MetricCriteria {
    public record IncrementLike(
            String eventId,
            String consumerGroup,
            String payload,
            Long productId
    ) {
    }

    public record DecrementLike(
            String eventId,
            String consumerGroup,
            String payload,
            Long productId
    ) {
    }
}
