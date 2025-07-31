package com.loopers.application.order;

import com.loopers.domain.order.OrderCommand;
import java.util.List;

public class OrderCriteria {
    public record Order(
            Long userId,
            List<Line> lines,
            Delivery delivery
    ) {
        public OrderCommand.Order toOrderCommandWith(List<OrderCommand.Line> lines) {
            return new OrderCommand.Order(userId, lines, toCommandDelivery());
        }

        public OrderCommand.Delivery toCommandDelivery() {
            return new OrderCommand.Delivery(
                    delivery.receiverName(),
                    delivery.phoneNumber(),
                    delivery.baseAddress(),
                    delivery.detailAddress(),
                    delivery.requirements()
            );
        }
    }

    public record Line(
            Long productId,
            Long quantity
    ) {
    }

    public record Delivery(
            String receiverName,
            String phoneNumber,
            String baseAddress,
            String detailAddress,
            String requirements
    ) {
    }

    public record Get(
            Long userId,
            Long orderId
    ) {
    }

    public record GetOrders(
            Long userId
    ) {
    }
}
