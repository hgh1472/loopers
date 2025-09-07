package com.loopers.interfaces.event.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.application.product.ProductApplicationEvent;
import com.loopers.application.product.ProductGlobalEvent;
import com.loopers.application.product.ProductGlobalEventPublisher;
import com.loopers.domain.event.EventCommand;
import com.loopers.domain.event.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ProductEventListener {
    private final ObjectMapper objectMapper;
    private final OutboxService outboxService;
    private final ProductGlobalEventPublisher productGlobalEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOutbox(ProductApplicationEvent.Viewed event) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(event);

        EventCommand.Save cmd = new EventCommand.Save(
                event.eventId(), ProductGlobalEvent.TOPIC.VIEWED, event.productId().toString(), payload, event.createdAt());

        outboxService.save(cmd);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ProductApplicationEvent.Viewed event) {
        ProductGlobalEvent.Viewed globalEvent = new ProductGlobalEvent.Viewed(
                event.eventId(), event.productId(), event.userId(), event.createdAt());

        productGlobalEventPublisher.publish(globalEvent);
    }
}
