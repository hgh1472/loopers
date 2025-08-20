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
        public static Request of(Payment payment, String callbackUrl) {
            return new Request(
                    payment.getOrderId().toString(),
                    payment.getCard().getType(),
                    payment.getCard().getCardNo().value(),
                    payment.getAmount().longValue(),
                    callbackUrl
            );
        }
    }
}
