package com.loopers.infrastructure.cache;

import com.loopers.domain.cache.CacheGlobalEvent;
import com.loopers.domain.cache.CacheGlobalEvent.TOPIC;
import com.loopers.domain.cache.CacheGlobalEventPublisher;
import com.loopers.domain.event.FailEvent;
import com.loopers.infrastructure.event.FailEventJpaRepository;
import com.loopers.message.KafkaMessage;
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
        KafkaMessage<CacheGlobalEvent.ProductEvict> message =
                KafkaMessage.of(event.productId().toString(), CacheGlobalEvent.TOPIC.PRODUCT_EVICT, event);

        kafkaTemplate.send(message.getTopic(), message.getAggregateId(), message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        FailEvent failEvent = new FailEvent(
                                message.getEventId(),
                                message.getTopic(),
                                message.getAggregateId(),
                                message.getPayload().toString(),
                                message.getTimestamp()
                        );

                        failEventJpaRepository.save(failEvent);
                    }
                });
    }
}
