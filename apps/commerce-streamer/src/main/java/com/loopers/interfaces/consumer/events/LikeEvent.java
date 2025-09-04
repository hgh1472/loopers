package com.loopers.interfaces.consumer.events;

import java.time.ZonedDateTime;

public class LikeEvent {
    public record Liked(
            String eventId,
            Long productId,
            Long userId,
            ZonedDateTime createdAt
    ) {
    }

    public record Canceled(
            String eventId,
            Long productId,
            Long userId,
            ZonedDateTime createdAt
    ) {
    }
}
