package com.loopers.interfaces.event.like;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.cache.CacheGlobalEvent;
import com.loopers.domain.cache.CacheGlobalEventPublisher;
import com.loopers.domain.event.EventCommand;
import com.loopers.domain.event.OutboxService;
import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.like.LikeGlobalEventPublisher;
import com.loopers.infrastructure.like.LikeGlobalEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class LikeEventListener {
    private final LikeGlobalEventPublisher likeGlobalEventPublisher;
    private final CacheGlobalEventPublisher cacheGlobalEventPublisher;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOutbox(LikeEvent.Liked event) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(event);

        EventCommand.Save cmd = new EventCommand.Save(
                event.eventId().toString(),
                LikeGlobalEvent.TOPIC_V1,
                event.productId().toString(),
                payload,
                event.createdAt()
        );

        outboxService.save(cmd);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOutbox(LikeEvent.LikeCanceled event) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(event);

        EventCommand.Save cmd = new EventCommand.Save(
                event.eventId().toString(),
                LikeGlobalEvent.TOPIC_V1,
                event.productId().toString(),
                payload,
                event.createdAt()
        );

        outboxService.save(cmd);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(LikeEvent.Liked event) {
        LikeGlobalEvent.Like globalEvent =
                new LikeGlobalEvent.Like(event.eventId().toString(), event.userId(), event.productId(), true, event.createdAt());
        likeGlobalEventPublisher.publish(globalEvent);
        cacheGlobalEventPublisher.publish(
                new CacheGlobalEvent.ProductEvict(event.eventId().toString(), event.productId(), event.createdAt()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(LikeEvent.LikeCanceled event) {
        LikeGlobalEvent.Like globalEvent =
                new LikeGlobalEvent.Like(event.eventId().toString(), event.userId(), event.productId(), false, event.createdAt());
        likeGlobalEventPublisher.publish(globalEvent);
        cacheGlobalEventPublisher.publish(
                new CacheGlobalEvent.ProductEvict(event.eventId().toString(), event.productId(), event.createdAt()));
    }
}
