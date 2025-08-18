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
}
