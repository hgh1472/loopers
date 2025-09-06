package com.loopers.infrastructure.like;

import com.loopers.domain.event.FailEvent;
import com.loopers.domain.like.LikeGlobalEventPublisher;
import com.loopers.infrastructure.event.FailEventJpaRepository;
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
        kafkaTemplate.send(LikeGlobalEvent.TOPIC_V1, event.productId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        FailEvent failEvent = new FailEvent(event.eventId(), LikeGlobalEvent.TOPIC_V1,
                                event.productId().toString(), event.toString(), event.createdAt());
                        failEventJpaRepository.save(failEvent);
                    }
                });
    }
}
