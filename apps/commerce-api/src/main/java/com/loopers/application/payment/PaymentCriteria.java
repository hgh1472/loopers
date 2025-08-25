package com.loopers.application.payment;

import java.util.UUID;

public class PaymentCriteria {

    public record Pay(
            UUID orderId,
            String cardType,
            String cardNo
    ) {
    }

    public record Success(
            String transactionKey,
            UUID orderId
    ) {
    }

    public record Fail(
            String transactionKey,
            UUID orderId,
            String reason
    ) {
    }
}
