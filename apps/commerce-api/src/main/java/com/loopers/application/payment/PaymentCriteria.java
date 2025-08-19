package com.loopers.application.payment;

public class PaymentCriteria {

    public record Success(
            String transactionKey,
            Long orderId
    ) {
    }
}
