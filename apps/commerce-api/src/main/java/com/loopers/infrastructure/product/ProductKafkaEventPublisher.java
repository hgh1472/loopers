package com.loopers.infrastructure.product;

import com.loopers.application.product.ProductGlobalEvent;
import com.loopers.application.product.ProductGlobalEvent.TOPIC;
import com.loopers.application.product.ProductGlobalEventPublisher;
import com.loopers.domain.event.FailEvent;
import com.loopers.infrastructure.event.FailEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductKafkaEventPublisher implements ProductGlobalEventPublisher {
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final FailEventJpaRepository failEventJpaRepository;

    @Override
    public void publish(ProductGlobalEvent.Viewed event) {
        kafkaTemplate.send(ProductGlobalEvent.TOPIC.VIEWED, event.productId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        FailEvent failEvent = new FailEvent(event.eventId(), TOPIC.VIEWED, event.productId().toString(),
                                event.toString(), event.createdAt());
                        failEventJpaRepository.save(failEvent);
                    }
                });
    }
}
