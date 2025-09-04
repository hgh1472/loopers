package com.loopers.infrastructure.cache;

import com.loopers.domain.cache.CacheGlobalEvent;
import com.loopers.domain.cache.CacheGlobalEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheGlobalEventPublisherImpl implements CacheGlobalEventPublisher {
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void publish(CacheGlobalEvent.ProductEvict event) {
        kafkaTemplate.send(CacheGlobalEvent.TOPIC.PRODUCT_EVICT, event.productId(), event);
    }
}
