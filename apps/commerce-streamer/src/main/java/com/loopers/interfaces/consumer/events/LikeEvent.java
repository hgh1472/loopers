package com.loopers.interfaces.consumer.events;

import java.time.ZonedDateTime;

public class LikeEvent {
    public record Like(
            String eventId,
            Long productId,
            Long userId,
            boolean liked,
            ZonedDateTime createdAt
    ) {
    }
}
