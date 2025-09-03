package com.loopers.infrastructure.like;

import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.like.LikeGlobalEventPublisher;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeKafkaEventPublisher implements LikeGlobalEventPublisher {
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void publish(LikeEvent.Liked event) {
        LikeGlobalEvent.Liked kafkaEvent =
                new LikeGlobalEvent.Liked(event.eventId(), event.productId(), event.userId(), ZonedDateTime.now());
        kafkaTemplate.send(LikeGlobalEvent.TOPIC.LIKED, kafkaEvent.productId().toString(), kafkaEvent);
    }

    @Override
    public void publish(LikeEvent.LikeCanceled event) {
        LikeGlobalEvent.Canceled kafkaEvent =
                new LikeGlobalEvent.Canceled(event.eventId(), event.productId(), event.userId(), ZonedDateTime.now());
        kafkaTemplate.send(LikeGlobalEvent.TOPIC.LIKE_CANCELED, kafkaEvent.productId().toString(), kafkaEvent);
    }
}
