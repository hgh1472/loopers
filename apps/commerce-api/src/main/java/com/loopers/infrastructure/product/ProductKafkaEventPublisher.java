package com.loopers.infrastructure.product;

import com.loopers.application.product.ProductGlobalEvent;
import com.loopers.application.product.ProductGlobalEventPublisher;
import com.loopers.domain.event.OutboxFailProcessor;
import com.loopers.domain.event.OutboxSuccessProcessor;
import com.loopers.message.KafkaMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductKafkaEventPublisher implements ProductGlobalEventPublisher {
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final OutboxFailProcessor outboxFailProcessor;
    private final OutboxSuccessProcessor outboxSuccessProcessor;

    @Override
    public void publish(ProductGlobalEvent.Viewed event) {
        KafkaMessage<ProductGlobalEvent.Viewed> message =
                KafkaMessage.of(event.productId().toString(), ProductGlobalEvent.TOPIC.VIEWED, event);

        kafkaTemplate.send(ProductGlobalEvent.TOPIC.VIEWED, message.getAggregateId(), message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        outboxFailProcessor.process(event.eventId(), ex.getMessage());
                        return;
                    }
                    outboxSuccessProcessor.process(event.eventId());
                });
    }
}
