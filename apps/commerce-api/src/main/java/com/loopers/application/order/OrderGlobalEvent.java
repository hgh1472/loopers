package com.loopers.application.order;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class OrderGlobalEvent {
    public static class TOPIC {
        public static final String PAID = "internal.order-paid-event.v1";
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
        public static OrderGlobalEvent.Paid from(OrderApplicationEvent.Paid event) {
            return new OrderGlobalEvent.Paid(
                    event.eventId(),
                    event.orderId(),
                    event.userId(),
                    event.couponId(),
                    event.transactionKey(),
                    event.lines().stream()
                            .map(line -> new Line(line.productId(), line.quantity()))
                            .toList(),
                    event.createdAt()
            );
        }
    }

    public record Line(
            Long productId,
            Long quantity
    ) {
    }
}
