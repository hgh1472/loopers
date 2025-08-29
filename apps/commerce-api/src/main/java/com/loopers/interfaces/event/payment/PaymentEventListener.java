package com.loopers.interfaces.event.payment;

import com.loopers.application.order.OrderApplicationEvent;
import com.loopers.application.payment.PaymentCriteria;
import com.loopers.application.payment.PaymentFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentFacade paymentFacade;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderApplicationEvent.Refund event) {
        PaymentCriteria.Refund criteria = new PaymentCriteria.Refund(
                event.userId(),
                event.couponId(),
                event.orderId(),
                event.transactionKey(),
                switch (event.reason()) {
                    case OUT_OF_STOCK -> PaymentCriteria.Refund.Reason.OUT_OF_STOCK;
                    case POINT_EXHAUSTED -> PaymentCriteria.Refund.Reason.POINT_EXHAUSTED;
                }
        );
        paymentFacade.refund(criteria);
    }
}
