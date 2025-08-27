package com.loopers.domain.payment;

import java.util.UUID;

public class PaymentEvent {

    public record Success(
            String transactionKey,
            UUID orderId
    ) {
        public static Success from(Payment payment) {
            return new Success(payment.getTransactionKey(), payment.getOrderId());
        }
    }
}
