package com.loopers.domain.order;

import java.math.BigDecimal;
import java.util.List;

public record OrderInfo(
        Long id,
        Long userId,
        String orderStatus,
        List<Line> lines,
        Delivery delivery,
        Payment payment
) {
    public static OrderInfo from(Order order) {
        return new OrderInfo(
                order.getId(),
                order.getUserId(),
                order.getStatus().name(),
                order.getOrderLines().stream()
                        .map(Line::from)
                        .toList(),
                Delivery.from(order.getOrderDelivery()),
                Payment.from(order.getOrderPayment())
        );
    }

    public record Line(
            Long productId,
            Long quantity,
            BigDecimal amount
    ) {
        public static Line from(OrderLine orderLine) {
            return new Line(
                    orderLine.getProductId(),
                    orderLine.getQuantity(),
                    orderLine.getAmount()
            );
        }
    }

    public record Delivery(
            String receiverName,
            String phoneNumber,
            String baseAddress,
            String detailAddress,
            String requirements
    ) {
        public static Delivery from(OrderDelivery orderDelivery) {
            return new Delivery(
                    orderDelivery.getReceiverName(),
                    orderDelivery.getPhoneNumber(),
                    orderDelivery.getBaseAddress(),
                    orderDelivery.getDetailAddress(),
                    orderDelivery.getRequirements()
            );
        }
    }

    public record Payment(
            BigDecimal originalAmount,
            BigDecimal paymentAmount
    ) {
        public static Payment from(OrderPayment orderPayment) {
            return new Payment(
                    orderPayment.getOriginalAmount(),
                    orderPayment.getPaymentAmount()
            );
        }
    }
}
