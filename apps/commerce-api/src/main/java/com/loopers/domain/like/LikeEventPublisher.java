package com.loopers.domain.like;

public interface LikeEventPublisher {
    void publish(LikeEvent.Liked event);
}
