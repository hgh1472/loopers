package com.loopers.interfaces.event.order;

import com.loopers.application.order.OrderApplicationEvent;
import com.loopers.application.order.OrderCriteria;
import com.loopers.application.order.OrderFacade;
import com.loopers.application.order.OrderGlobalEvent;
import com.loopers.application.order.OrderGlobalEventPublisher;
import com.loopers.domain.payment.PaymentEvent;
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

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderApplicationEvent.Paid event) {
        orderGlobalEventPublisher.publish(OrderGlobalEvent.Paid.from(event));
    }
}
