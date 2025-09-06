package com.loopers.domain.like;

import com.loopers.infrastructure.like.LikeGlobalEvent;

public interface LikeGlobalEventPublisher {
    void publish(LikeGlobalEvent.Like event);
}
