package com.loopers.domain.cache;

public interface CacheGlobalEventPublisher {
    void publish(CacheGlobalEvent.ProductEvict event);
}
