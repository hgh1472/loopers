package com.loopers.infrastructure.cache;

import com.loopers.domain.cache.CacheGlobalEvent;
import com.loopers.domain.cache.CacheGlobalEventPublisher;
import com.loopers.domain.event.OutboxFailProcessor;
import com.loopers.domain.event.OutboxSuccessProcessor;
import com.loopers.message.KafkaMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheKafkaEventPublisher implements CacheGlobalEventPublisher {
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final OutboxFailProcessor outboxFailProcessor;
    private final OutboxSuccessProcessor outboxSuccessProcessor;

    @Override
    public void publish(CacheGlobalEvent.ProductEvict event) {
        KafkaMessage<CacheGlobalEvent.ProductEvict> message =
                KafkaMessage.of(event.productId().toString(), CacheGlobalEvent.TOPIC.PRODUCT_EVICT, event);

        kafkaTemplate.send(message.getTopic(), message.getAggregateId(), message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        outboxFailProcessor.process(event.eventId(), ex.getMessage());
                        return;
                    }
                    outboxSuccessProcessor.process(event.eventId());
                });
    }
}
