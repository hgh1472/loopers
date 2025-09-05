package com.loopers.infrastructure.order;

import com.loopers.application.order.OrderGlobalEvent;
import com.loopers.application.order.OrderGlobalEventPublisher;
import com.loopers.domain.event.FailEvent;
import com.loopers.infrastructure.event.FailEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderKafkaEventPublisher implements OrderGlobalEventPublisher {
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final FailEventJpaRepository failEventJpaRepository;

    @Override
    public void publish(OrderGlobalEvent.Paid event) {
        kafkaTemplate.send(OrderGlobalEvent.TOPIC.PAID, event.eventId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        var failEvent = new FailEvent(event.eventId(), OrderGlobalEvent.TOPIC.PAID, event.orderId().toString(),
                                event.toString(), event.createdAt());
                        failEventJpaRepository.save(failEvent);
                    }
                });
    }
}
