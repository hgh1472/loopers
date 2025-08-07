package com.loopers.application.order;

import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.stock.StockCommand;
import java.math.BigDecimal;
import java.util.List;

public class OrderCriteria {
    public record Order(
            Long userId,
            List<Line> lines,
            Delivery delivery,
            Long couponId
    ) {
        public OrderCommand.Order toOrderCommandWith(List<OrderCommand.Line> lines,
                                                     BigDecimal originalAmount, BigDecimal paymentAmount) {
            return new OrderCommand.Order(userId, lines, toCommandDelivery(), originalAmount, paymentAmount);
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

        public List<StockCommand.Deduct> toCommandDeduct() {
            return lines.stream()
                    .map(line -> new StockCommand.Deduct(line.productId(), line.quantity()))
                    .toList();
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
