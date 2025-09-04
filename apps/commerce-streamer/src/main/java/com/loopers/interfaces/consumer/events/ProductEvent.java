package com.loopers.interfaces.consumer.events;

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
