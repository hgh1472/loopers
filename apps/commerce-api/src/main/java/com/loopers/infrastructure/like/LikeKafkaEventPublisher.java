package com.loopers.infrastructure.like;

import com.loopers.domain.event.OutboxFailProcessor;
import com.loopers.domain.event.OutboxSuccessProcessor;
import com.loopers.domain.like.LikeGlobalEventPublisher;
import com.loopers.message.KafkaMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeKafkaEventPublisher implements LikeGlobalEventPublisher {
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final OutboxFailProcessor outboxFailProcessor;
    private final OutboxSuccessProcessor outboxSuccessProcessor;

    @Override
    public void publish(LikeGlobalEvent.Like event) {
        KafkaMessage<LikeGlobalEvent.Like> message =
                KafkaMessage.of(event.productId().toString(), LikeGlobalEvent.TOPIC_V1, event);

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
