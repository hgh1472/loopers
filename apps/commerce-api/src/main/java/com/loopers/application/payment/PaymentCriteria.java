package com.loopers.application.payment;

public class PaymentCriteria {

    public record Success(
            String transactionKey,
            Long orderId
    ) {
    }

    public record Fail(
            String transactionKey,
            Long orderId,
            String reason
    ) {
    }
}
