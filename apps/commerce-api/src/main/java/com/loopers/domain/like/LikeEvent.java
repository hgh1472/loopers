package com.loopers.domain.like;

import com.loopers.support.event.UserActivityEvent;
import java.time.ZonedDateTime;
import java.util.UUID;

public class LikeEvent {

    public record Liked(
            UUID eventId,
            Long productId,
            Long userId,
            ZonedDateTime createdAt
    ) implements UserActivityEvent {
    }

    public record LikeCanceled(
            UUID eventId,
            Long productId,
            Long userId,
            ZonedDateTime createdAt
    ) implements UserActivityEvent {
    }
}
