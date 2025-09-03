package com.loopers.interfaces.consumer.metrics;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class OrderEvent {
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
