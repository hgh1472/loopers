package com.loopers.application.order;

import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.stock.StockCommand;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderCriteria {
    public record Order(
            Long userId,
            List<Line> lines,
            Delivery delivery,
            Long couponId,
            Long point
    ) {
        public OrderCommand.Order toOrderCommandWith(List<OrderCommand.Line> lines, Long couponId, AmountResult amountResult) {
            return new OrderCommand.Order(userId, couponId, lines, toCommandDelivery(), amountResult.originalAmount(),
                    amountResult.discountAmount(), amountResult.pointAmount());
        }

        public List<OrderCommand.Line> toCommandLines(Map<Long, BigDecimal> productPriceMap) {
            return lines.stream()
                    .map(line -> new OrderCommand.Line(
                            line.productId(),
                            line.quantity(),
                            productPriceMap.get(line.productId()).multiply(BigDecimal.valueOf(line.quantity()))
                    ))
                    .toList();
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
            UUID orderId
    ) {
    }

    public record GetOrders(
            Long userId
    ) {
    }

    public record Expire(
            ZonedDateTime time
    ) {
    }
}
