package com.loopers.interfaces.event.platform;

import com.loopers.application.order.OrderApplicationEvent;
import com.loopers.domain.order.OrderEvent;
import com.loopers.domain.payment.PaymentEvent;
import com.loopers.domain.platform.DataPlatformService;
import com.loopers.domain.platform.PlatformCommand;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PlatformEventListener {
    private final DataPlatformService dataPlatformService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderEvent.Created event) {
        dataPlatformService.send(new PlatformCommand.Order(event.orderId(), PlatformCommand.Order.Status.CREATED));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PaymentEvent.Success event) {
        dataPlatformService.send(
                new PlatformCommand.Payment(event.orderId(), event.transactionKey(), PlatformCommand.Payment.Status.SUCCESS));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PaymentEvent.Fail event) {
        dataPlatformService.send(
                new PlatformCommand.Payment(event.orderId(), event.transactionKey(), PlatformCommand.Payment.Status.FAILED));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderApplicationEvent.Expired event) {
        List<PlatformCommand.Order> commands = event.orderIds().stream()
                .map(orderId -> new PlatformCommand.Order(orderId, PlatformCommand.Order.Status.EXPIRED))
                .toList();
        dataPlatformService.send(commands);
    }
}
