package com.loopers.interfaces.event.count;

import com.loopers.domain.count.ProductCountCommand;
import com.loopers.domain.count.ProductCountService;
import com.loopers.domain.like.LikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CountEventListener {
    private final ProductCountService productCountService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handle(LikeEvent.Liked event) {
        productCountService.incrementLike(new ProductCountCommand.Increment(event.productId()));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handle(LikeEvent.LikeCanceled event) {
        productCountService.decrementLike(new ProductCountCommand.Decrement(event.productId()));
    }
}
