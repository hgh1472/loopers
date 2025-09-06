package com.loopers.infrastructure.like;

import com.loopers.domain.event.FailEvent;
import com.loopers.domain.like.LikeGlobalEventPublisher;
import com.loopers.infrastructure.event.FailEventJpaRepository;
import com.loopers.message.KafkaMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeKafkaEventPublisher implements LikeGlobalEventPublisher {
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final FailEventJpaRepository failEventJpaRepository;

    @Override
    public void publish(LikeGlobalEvent.Like event) {
        KafkaMessage<LikeGlobalEvent.Like> message =
                KafkaMessage.of(event.productId().toString(), LikeGlobalEvent.TOPIC_V1, event);

        kafkaTemplate.send(message.getTopic(), message.getAggregateId(), message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        FailEvent failEvent = new FailEvent(
                                message.getEventId(),
                                message.getTopic(),
                                message.getAggregateId(),
                                message.getPayload().toString(),
                                message.getTimestamp()
                        );

                        failEventJpaRepository.save(failEvent);
                    }
                });
    }
}
