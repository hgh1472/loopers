package com.loopers.infrastructure.cache;

import com.loopers.domain.cache.CacheGlobalEvent;
import com.loopers.domain.cache.CacheGlobalEvent.TOPIC;
import com.loopers.domain.cache.CacheGlobalEventPublisher;
import com.loopers.domain.event.FailEvent;
import com.loopers.infrastructure.event.FailEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheKafkaEventPublisher implements CacheGlobalEventPublisher {
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final FailEventJpaRepository failEventJpaRepository;

    @Override
    public void publish(CacheGlobalEvent.ProductEvict event) {
        kafkaTemplate.send(CacheGlobalEvent.TOPIC.PRODUCT_EVICT, event.productId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        FailEvent failEvent = new FailEvent(event.eventId(), TOPIC.PRODUCT_EVICT, event.productId().toString(),
                                event.toString(), event.createdAt());
                        failEventJpaRepository.save(failEvent);
                    }
                });
    }
}
