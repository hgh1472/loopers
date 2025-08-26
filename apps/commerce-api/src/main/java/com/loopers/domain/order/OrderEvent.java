package com.loopers.domain.order;

import java.util.UUID;

public class OrderEvent {
    public record Created(
            UUID orderId,
            Long userId,
            Long couponId
    ) {
        public static OrderEvent.Created from(Order order) {
            return new OrderEvent.Created(
                    order.getId(),
                    order.getUserId(),
                    order.getCouponId()
            );
        }
    }
}
