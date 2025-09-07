package com.loopers.application.product;

import java.time.ZonedDateTime;

public class ProductApplicationEvent {
    public record Viewed(
            String eventId,
            Long productId,
            Long userId,
            ZonedDateTime createdAt
    ) {
    }
}
