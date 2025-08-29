package com.loopers.infrastructure.point;

import com.loopers.domain.point.PointEvent;
import com.loopers.domain.point.PointEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointCoreEventPublisher implements PointEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(PointEvent.Charged event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(PointEvent.Used event) {
        applicationEventPublisher.publishEvent(event);
    }
}
