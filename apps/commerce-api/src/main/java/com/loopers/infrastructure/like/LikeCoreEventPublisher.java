package com.loopers.infrastructure.like;

import com.loopers.domain.like.LikeEvent.Liked;
import com.loopers.domain.like.LikeEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeCoreEventPublisher implements LikeEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(Liked event) {
        applicationEventPublisher.publishEvent(event);
    }
}
