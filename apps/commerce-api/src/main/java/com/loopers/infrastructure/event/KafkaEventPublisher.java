package com.loopers.infrastructure.event;

import com.loopers.domain.event.GlobalEventPublisher;
import com.loopers.domain.event.Outbox;
import com.loopers.domain.event.OutboxFailProcessor;
import com.loopers.domain.event.OutboxSuccessProcessor;
import com.loopers.message.KafkaMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher implements GlobalEventPublisher {
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final OutboxSuccessProcessor outboxSuccessProcessor;
    private final OutboxFailProcessor outboxFailProcessor;

    @Override
    public void publish(Outbox outbox) {
        KafkaMessage<Object> message = KafkaMessage.of(outbox.getAggregateId(), outbox.getTopic(), outbox.getPayload());

        kafkaTemplate.send(outbox.getTopic(), outbox.getAggregateId(), message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        outboxFailProcessor.process(outbox.getEventId(), ex.getMessage());
                        return;
                    }
                    outboxSuccessProcessor.process(outbox.getEventId());
                });
    }
}
