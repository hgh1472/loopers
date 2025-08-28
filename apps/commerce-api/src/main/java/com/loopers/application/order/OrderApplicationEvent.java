package com.loopers.application.order;

import java.util.UUID;

public class OrderApplicationEvent {
    public record Refund(
            UUID orderId,
            Long couponId,
            String transactionKey,
            Long userId,
            Reason reason
    ) {
        public enum Reason {
            OUT_OF_STOCK,
            POINT_EXHAUSTED
        }
    }
}
