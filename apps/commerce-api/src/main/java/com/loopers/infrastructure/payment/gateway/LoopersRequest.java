package com.loopers.infrastructure.payment.gateway;

import com.loopers.domain.payment.Card;
import com.loopers.domain.payment.Payment;

public class LoopersRequest {
    public record Request(
            String orderId,
            Card.Type cardType,
            String cardNo,
            Long amount,
            String callbackUrl
    ) {
        public static Request of(Payment payment, Card card, String callbackUrl) {
            return new Request(
                    payment.getOrderId().toString(),
                    card.getType(),
                    card.getCardNo().value(),
                    payment.getAmount().longValue(),
                    callbackUrl
            );
        }
    }
}
