package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.PaymentResult;
import java.math.BigDecimal;
import java.util.UUID;

public class PaymentV1Dto {

    public record PaymentRequest(
            UUID orderId,
            String cardType,
            String cardNo
    ) {
    }

    public record PaymentResponse(
            String transactionKey,
            UUID orderId,
            BigDecimal amount,
            String status,
            String reason
    ) {
        public static PaymentResponse from(PaymentResult result) {
            return new PaymentResponse(
                    result.transactionKey(),
                    result.orderId(),
                    result.paymentAmount(),
                    result.status().name(),
                    result.reason()
            );
        }
    }

    public record CallbackRequest(
            String transactionKey,
            String orderId,
            String card,
            String cardNo,
            Long amount,
            Status status,
            String reason
    ) {
    }

    public enum Status {
        SUCCESS, FAILED
    }
}
