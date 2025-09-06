package com.loopers.domain.cache;

import java.time.ZonedDateTime;

public class CacheGlobalEvent {
    public static class TOPIC {
        public static final String PRODUCT_EVICT = "internal.cache.product-evict-command.v1";
    }

    public record ProductEvict(
            String eventId,
            Long productId,
            ZonedDateTime createdAt
    ) {
    }
}
