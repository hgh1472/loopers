package com.loopers.infrastructure.like;

import java.time.ZonedDateTime;
import java.util.UUID;

public class LikeGlobalEvent {
    public static final class TOPIC {
        public static final String LIKED = "internal.liked-events.v1";
        public static final String LIKE_CANCELED = "internal.like-canceled-events.v1";
    }

    public record Liked(
            UUID eventId,
            Long productId,
            Long userId,
            ZonedDateTime createdAt
    ) {
    }

    public record Canceled(
            UUID eventId,
            Long productId,
            Long userId,
            ZonedDateTime createdAt
    ) {
    }
}
