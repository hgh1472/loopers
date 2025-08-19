package com.loopers.domain.payment;

import java.math.BigDecimal;

public record RefundInfo(
        String transactionKey,
        Long paymentId,
        Long orderId,
        BigDecimal amount,
        Card.Type cardType,
        String cardNo
) {
    public static RefundInfo of(Refund refund) {
        return new RefundInfo(
                refund.getTransactionKey(),
                refund.getPaymentId(),
                refund.getOrderId(),
                refund.getAmount(),
                refund.getCard().getType(),
                refund.getCard().getCardNo().value()
        );
    }
}
