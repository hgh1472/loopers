package com.loopers.domain.point;

public interface PointEventPublisher {
    void publish(PointEvent.Charged event);

    void publish(PointEvent.Used event);
}
