package com.loopers.interfaces.event.order;

import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderEvent;
import com.loopers.domain.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderService orderService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderEvent.Created event) {
        orderService.sendOrder(new OrderCommand.Send(event.orderId()));
    }
}
