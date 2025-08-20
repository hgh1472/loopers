package com.loopers.interfaces.api.payment;

public class PaymentV1Dto {
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
