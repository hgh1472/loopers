package com.loopers.interfaces.event.like;

import com.loopers.domain.cache.CacheGlobalEvent;
import com.loopers.domain.cache.CacheGlobalEventPublisher;
import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.like.LikeGlobalEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class LikeEventListener {
    private final LikeGlobalEventPublisher likeGlobalEventPublisher;
    private final CacheGlobalEventPublisher cacheGlobalEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(LikeEvent.Liked event) {
        likeGlobalEventPublisher.publish(event);
        cacheGlobalEventPublisher.publish(
                new CacheGlobalEvent.ProductEvict(event.eventId().toString(), event.productId(), event.createdAt()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(LikeEvent.LikeCanceled event) {
        likeGlobalEventPublisher.publish(event);
        cacheGlobalEventPublisher.publish(
                new CacheGlobalEvent.ProductEvict(event.eventId().toString(), event.productId(), event.createdAt()));
    }
}
