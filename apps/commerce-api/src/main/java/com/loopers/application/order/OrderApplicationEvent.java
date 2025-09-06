package com.loopers.application.order;

import com.loopers.support.event.UserActivityEvent;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class OrderApplicationEvent {
    public record Created(
            UUID orderId,
            Long userId,
            Long couponId
    ) implements UserActivityEvent {
    }

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
            String eventId,
            UUID orderId,
            Long userId,
            Long couponId,
            String transactionKey,
            List<Line> lines,
            ZonedDateTime createdAt
    ) {
    }

    public record Line(
            Long productId,
            Long quantity
    ) {
    }
}
