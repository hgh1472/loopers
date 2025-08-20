package com.loopers.domain.payment;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentCommand {

    public record Pay(
            BigDecimal amount,
            UUID orderId,
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

    public record Fail(
            String transactionKey,
            String reason
    ) {
    }
}
