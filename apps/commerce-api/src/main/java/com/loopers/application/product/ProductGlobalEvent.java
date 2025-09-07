package com.loopers.application.product;

import java.time.ZonedDateTime;

public class ProductGlobalEvent {
    public static class TOPIC {
        public static final String VIEWED = "internal.product-viewed-event.v1";
    }
    public record Viewed(
            String eventId,
            Long productId,
            Long userId,
            ZonedDateTime createdAt
    ) {
    }
}
