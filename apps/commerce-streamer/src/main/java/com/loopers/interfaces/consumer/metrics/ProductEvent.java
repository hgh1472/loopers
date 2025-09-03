package com.loopers.interfaces.consumer.metrics;

import java.time.ZonedDateTime;

public class ProductEvent {
    public record Viewed(
            String eventId,
            Long productId,
            Long userId,
            ZonedDateTime createdAt
    ) {
    }
}
