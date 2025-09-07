package com.loopers.interfaces.event.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.application.order.OrderApplicationEvent;
import com.loopers.application.order.OrderCriteria;
import com.loopers.application.order.OrderFacade;
import com.loopers.application.order.OrderGlobalEvent;
import com.loopers.application.order.OrderGlobalEventPublisher;
import com.loopers.domain.cache.CacheGlobalEvent;
import com.loopers.domain.cache.CacheGlobalEventPublisher;
import com.loopers.domain.event.EventCommand;
import com.loopers.domain.event.OutboxService;
import com.loopers.domain.payment.PaymentEvent;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderFacade orderFacade;
    private final OrderGlobalEventPublisher orderGlobalEventPublisher;
    private final CacheGlobalEventPublisher cacheGlobalEventPublisher;
    private final ObjectMapper objectMapper;
    private final OutboxService outboxService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PaymentEvent.Success event) {
        orderFacade.succeedPayment(new OrderCriteria.Success(event.orderId(), event.transactionKey()));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PaymentEvent.Fail event) {
        orderFacade.failPayment(new OrderCriteria.FailPayment(event.orderId()));
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOutbox(OrderApplicationEvent.Paid event) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(event);

        EventCommand.Save cmd = new EventCommand.Save(
                event.eventId(),
                OrderGlobalEvent.TOPIC.PAID,
                event.orderId().toString(),
                payload,
                event.createdAt()
        );

        outboxService.save(cmd);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderApplicationEvent.Paid event) {
        orderGlobalEventPublisher.publish(OrderGlobalEvent.Paid.from(event));
        for (OrderApplicationEvent.Line line : event.lines()) {
            cacheGlobalEventPublisher.publish(
                    new CacheGlobalEvent.ProductEvict(UUID.randomUUID().toString(), line.productId(), event.createdAt()));
        }
    }
}
