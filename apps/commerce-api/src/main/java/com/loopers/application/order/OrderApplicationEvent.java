package com.loopers.application.order;

import java.util.List;
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

    public record Expired(
            List<UUID> orderIds
    ) {
    }

    public record Paid(
            UUID orderId,
            String transactionKey
    ) {
    }
}
