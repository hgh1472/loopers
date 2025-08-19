package com.loopers.domain.payment;

import java.math.BigDecimal;

public class PaymentCommand {

    public record Pay(
            BigDecimal amount,
            Long orderId,
            String cardType,
            String cardNo
    ) {
    }

    public record Refund(
            String transactionKey
    ) {
    }

    public record Success(
            String transactionKey
    ) {
    }
}
