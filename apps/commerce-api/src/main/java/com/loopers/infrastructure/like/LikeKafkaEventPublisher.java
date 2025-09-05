package com.loopers.infrastructure.like;

import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.like.LikeGlobalEventPublisher;
import com.loopers.infrastructure.event.FailEventJpaRepository;
import com.loopers.infrastructure.like.LikeGlobalEvent.TOPIC;
import com.loopers.domain.event.FailEvent;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeKafkaEventPublisher implements LikeGlobalEventPublisher {
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final FailEventJpaRepository failEventJpaRepository;

    @Override
    public void publish(LikeEvent.Liked event) {
        LikeGlobalEvent.Liked kafkaEvent =
                new LikeGlobalEvent.Liked(event.eventId(), event.productId(), event.userId(), ZonedDateTime.now());
        kafkaTemplate.send(LikeGlobalEvent.TOPIC.LIKED, kafkaEvent.productId().toString(), kafkaEvent)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        FailEvent failEvent = new FailEvent(event.eventId().toString(), TOPIC.LIKED, event.productId().toString(),
                                event.toString(), event.createdAt());
                        failEventJpaRepository.save(failEvent);
                    }
                });
    }

    @Override
    public void publish(LikeEvent.LikeCanceled event) {
        LikeGlobalEvent.Canceled kafkaEvent =
                new LikeGlobalEvent.Canceled(event.eventId(), event.productId(), event.userId(), ZonedDateTime.now());
        kafkaTemplate.send(LikeGlobalEvent.TOPIC.LIKE_CANCELED, kafkaEvent.productId().toString(), kafkaEvent)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        FailEvent failEvent = new FailEvent(event.eventId().toString(), TOPIC.LIKE_CANCELED,
                                event.productId().toString(), event.toString(), event.createdAt());
                        failEventJpaRepository.save(failEvent);
                    }
                });
    }
}
