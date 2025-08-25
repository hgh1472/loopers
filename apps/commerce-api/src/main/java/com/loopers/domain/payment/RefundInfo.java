package com.loopers.domain.payment;

import java.math.BigDecimal;
import java.util.UUID;

public record RefundInfo(
        String transactionKey,
        Long paymentId,
        UUID orderId,
        BigDecimal amount
) {
    public static RefundInfo of(Refund refund) {
        return new RefundInfo(
                refund.getTransactionKey(),
                refund.getPaymentId(),
                refund.getOrderId(),
                refund.getAmount()
        );
    }
}
