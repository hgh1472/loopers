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

    public record Fail(
            String transactionKey,
            UUID orderId,
            String reason
    ) {
        public static Fail from(Payment payment) {
            return new Fail(payment.getTransactionKey(), payment.getOrderId(), payment.getReason());
        }
    }
}
