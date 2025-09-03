package com.loopers.infrastructure.product;

import com.loopers.application.product.ProductGlobalEvent;
import com.loopers.application.product.ProductGlobalEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductGlobalEventPublisherImpl implements ProductGlobalEventPublisher {
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void publish(ProductGlobalEvent.Viewed event) {
        kafkaTemplate.send(ProductGlobalEvent.TOPIC.VIEWED, event.productId().toString(), event);
    }
}
