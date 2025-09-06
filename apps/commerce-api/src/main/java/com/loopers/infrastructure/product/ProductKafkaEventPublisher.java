package com.loopers.infrastructure.product;

import com.loopers.application.product.ProductGlobalEvent;
import com.loopers.application.product.ProductGlobalEventPublisher;
import com.loopers.domain.event.FailEvent;
import com.loopers.infrastructure.event.FailEventJpaRepository;
import com.loopers.message.KafkaMessage;
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
        KafkaMessage<ProductGlobalEvent.Viewed> message =
                KafkaMessage.of(event.productId().toString(), ProductGlobalEvent.TOPIC.VIEWED, event);

        kafkaTemplate.send(ProductGlobalEvent.TOPIC.VIEWED, message.getAggregateId(), message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        FailEvent failEvent = new FailEvent(
                                message.getEventId(),
                                ProductGlobalEvent.TOPIC.VIEWED,
                                message.getAggregateId(),
                                message.getPayload().toString(),
                                message.getTimestamp()
                        );

                        failEventJpaRepository.save(failEvent);
                    }
                });
    }
}
