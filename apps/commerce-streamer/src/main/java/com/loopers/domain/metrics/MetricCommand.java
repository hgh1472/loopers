package com.loopers.domain.metrics;

public class MetricCommand {
    public record IncrementLike(
            Long productId
    ) {
    }

    public record DecrementLike(
            Long productId
    ) {
    }
}
