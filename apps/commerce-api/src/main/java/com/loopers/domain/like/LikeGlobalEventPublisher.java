package com.loopers.domain.like;

public interface LikeGlobalEventPublisher {
    void publish(LikeEvent.Liked event);

    void publish(LikeEvent.LikeCanceled event);
}
