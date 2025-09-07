package com.loopers.infrastructure.order;

import com.loopers.application.order.OrderGlobalEvent;
import com.loopers.application.order.OrderGlobalEventPublisher;
import com.loopers.domain.event.OutboxFailProcessor;
import com.loopers.domain.event.OutboxSuccessProcessor;
import com.loopers.message.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaEventPublisher implements OrderGlobalEventPublisher {
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final OutboxFailProcessor outboxFailProcessor;
    private final OutboxSuccessProcessor outboxSuccessProcessor;

    @Override
    public void publish(OrderGlobalEvent.Paid event) {
        KafkaMessage<OrderGlobalEvent.Paid> message =
                KafkaMessage.of(event.orderId().toString(), OrderGlobalEvent.TOPIC.PAID, event);

        kafkaTemplate.send(message.getTopic(), event.eventId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        outboxFailProcessor.process(event.eventId(), ex.getMessage());
                        return;
                    }
                    outboxSuccessProcessor.process(event.eventId());
                });
    }
}
