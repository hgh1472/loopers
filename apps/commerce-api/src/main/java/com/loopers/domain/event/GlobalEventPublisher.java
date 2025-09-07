package com.loopers.domain.event;

public interface GlobalEventPublisher {
    void publish(Outbox outbox);
}
