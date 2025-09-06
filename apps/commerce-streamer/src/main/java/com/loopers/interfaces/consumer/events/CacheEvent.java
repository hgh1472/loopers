package com.loopers.interfaces.consumer.events;

import java.time.ZonedDateTime;

public class CacheEvent {
    public record ProductEvict(
            String eventId,
            Long productId,
            ZonedDateTime createdAt
    ) {
    }
}
