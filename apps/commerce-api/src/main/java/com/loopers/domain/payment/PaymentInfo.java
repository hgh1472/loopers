package com.loopers.domain.payment;

import java.math.BigDecimal;

public record PaymentInfo(
        String transactionKey,
        Long orderId,
        BigDecimal amount,
        String cardNo,
        Payment.CardType cardType,
        Payment.Status status,
        String reason
) {
    public static PaymentInfo of(Payment payment) {
        return new PaymentInfo(
                payment.getTransactionKey(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getCardNo().value(),
                payment.getCardType(),
                payment.getStatus(),
                payment.getReason()
        );
    }
}
