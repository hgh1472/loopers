package com.loopers.domain.like;

import com.loopers.support.event.UserActivityEvent;

public class LikeEvent {
    public record Liked(Long productId, Long userId) implements UserActivityEvent {
    }

    public record LikeCanceled(Long productId, Long userId) implements UserActivityEvent {
    }
}
