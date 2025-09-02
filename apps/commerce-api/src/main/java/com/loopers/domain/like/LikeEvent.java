package com.loopers.domain.like;

import com.loopers.support.event.UserActivityEvent;

public class LikeEvent {
    public static final class TOPIC {
        public static final String LIKED = "internal.liked-events.v1";
        public static final String LIKE_CANCELED = "internal.like-canceled-events.v1";
    }

    public record Liked(Long productId, Long userId) implements UserActivityEvent {
    }

    public record LikeCanceled(Long productId, Long userId) implements UserActivityEvent {
    }
}
