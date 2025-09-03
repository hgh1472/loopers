package com.loopers.infrastructure.order;

import com.loopers.application.order.OrderGlobalEvent;
import com.loopers.application.order.OrderGlobalEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderGlobalEventPublisherImpl implements OrderGlobalEventPublisher {
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void publish(OrderGlobalEvent.Paid event) {
        kafkaTemplate.send(OrderGlobalEvent.TOPIC.PAID, event.eventId(), event);
    }
}
