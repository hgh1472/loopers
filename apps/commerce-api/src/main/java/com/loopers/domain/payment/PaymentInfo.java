package com.loopers.domain.payment;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentInfo(
        String transactionKey,
        UUID orderId,
        BigDecimal amount,
        String cardNo,
        Card.Type cardType,
        Payment.Status status,
        String reason
) {
    public static PaymentInfo of(Payment payment) {
        return new PaymentInfo(
                payment.getTransactionKey(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getCard().getCardNo().value(),
                payment.getCard().getType(),
                payment.getStatus(),
                payment.getReason()
        );
    }

    public record Transaction(
            String transactionKey,
            Payment.Status status,
            UUID orderId,
            String reason
    ) {
        public static Transaction of(GatewayResponse.Transaction transaction) {
            return new Transaction(
                    transaction.transactionKey(),
                    transaction.status(),
                    transaction.orderId(),
                    transaction.reason()
            );
        }
    }
}
