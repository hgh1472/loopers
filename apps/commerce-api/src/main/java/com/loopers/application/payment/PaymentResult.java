package com.loopers.application.payment;

import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentInfo;
import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResult(
        UUID orderId,
        String transactionKey,
        BigDecimal paymentAmount,
        Payment.Status status,
        String reason
) {
    public static PaymentResult from(PaymentInfo paymentInfo) {
        return new PaymentResult(
                paymentInfo.orderId(),
                paymentInfo.transactionKey(),
                paymentInfo.amount(),
                paymentInfo.status(),
                paymentInfo.reason()
        );
    }
}
