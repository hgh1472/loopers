package com.loopers.interfaces.event.order;

import com.loopers.application.order.OrderCriteria;
import com.loopers.application.order.SuccessProcessor;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderEvent;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderService orderService;
    private final SuccessProcessor successProcessor;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderEvent.Created event) {
        orderService.sendOrder(new OrderCommand.Send(event.orderId()));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PaymentEvent.Success event) {
        successProcessor.success(new OrderCriteria.Success(event.orderId(), event.transactionKey()));
    }
}
