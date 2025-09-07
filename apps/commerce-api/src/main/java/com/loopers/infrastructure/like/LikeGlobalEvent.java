package com.loopers.infrastructure.like;

import java.time.ZonedDateTime;

public class LikeGlobalEvent {
    public static final String TOPIC_V1 = "internal.like-events.v1";

    public record Like(
            String eventId,
            Long productId,
            Long userId,
            boolean liked,
            ZonedDateTime createdAt
    ) {
    }
}
