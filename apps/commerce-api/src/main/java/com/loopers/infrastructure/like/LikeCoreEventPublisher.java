package com.loopers.infrastructure.like;

import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.like.LikeEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeCoreEventPublisher implements LikeEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(LikeEvent.Liked event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(LikeEvent.LikeCanceled event) {
        applicationEventPublisher.publishEvent(event);
    }
}
