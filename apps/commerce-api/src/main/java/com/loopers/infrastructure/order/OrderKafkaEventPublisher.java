package com.loopers.infrastructure.order;

import com.loopers.application.order.OrderGlobalEvent;
import com.loopers.application.order.OrderGlobalEventPublisher;
import com.loopers.domain.event.FailEvent;
import com.loopers.infrastructure.event.FailEventJpaRepository;
import com.loopers.message.KafkaMessage;
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
        KafkaMessage<OrderGlobalEvent.Paid> message =
                KafkaMessage.of(event.orderId().toString(), OrderGlobalEvent.TOPIC.PAID, event);

        kafkaTemplate.send(message.getTopic(), event.eventId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        FailEvent failEvent = new FailEvent(
                                message.getEventId(),
                                OrderGlobalEvent.TOPIC.PAID,
                                message.getAggregateId(),
                                message.getPayload().toString(),
                                message.getTimestamp()
                        );

                        failEventJpaRepository.save(failEvent);
                    }
                });
    }
}
