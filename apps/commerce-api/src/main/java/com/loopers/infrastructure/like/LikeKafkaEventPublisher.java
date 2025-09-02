package com.loopers.infrastructure.like;

import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.like.LikeGlobalEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeKafkaEventPublisher implements LikeGlobalEventPublisher {
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void publish(LikeEvent.Liked event) {
        kafkaTemplate.send(LikeEvent.TOPIC.LIKED, event.productId().toString(), event);
    }

    @Override
    public void publish(LikeEvent.LikeCanceled event) {
        kafkaTemplate.send(LikeEvent.TOPIC.LIKE_CANCELED, event.productId().toString(), event);
    }
}
