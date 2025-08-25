package com.loopers.application.order;

import com.loopers.domain.order.OrderInfo;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderResult(
        UUID id,
        String orderStatus,
        List<Line> lines,
        Delivery delivery,
        Payment payment
) {
    public static OrderResult from(OrderInfo orderInfo) {
        return new OrderResult(
                orderInfo.id(),
                orderInfo.orderStatus(),
                orderInfo.lines().stream()
                        .map(OrderResult.Line::from)
                        .toList(),
                OrderResult.Delivery.from(orderInfo.delivery()),
                OrderResult.Payment.from(orderInfo.payment())
        );
    }

    public record Line(
            Long productId,
            Long quantity,
            BigDecimal amount
    ) {
        public static Line from(OrderInfo.Line line) {
            return new Line(
                    line.productId(),
                    line.quantity(),
                    line.amount()
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
        public static Delivery from(OrderInfo.Delivery delivery) {
            return new Delivery(
                    delivery.receiverName(),
                    delivery.phoneNumber(),
                    delivery.baseAddress(),
                    delivery.detailAddress(),
                    delivery.requirements()
            );
        }
    }

    public record Payment(
            BigDecimal originalAmount,
            BigDecimal paymentAmount
    ) {
        public static Payment from(OrderInfo.Payment payment) {
            return new Payment(
                    payment.originalAmount(),
                    payment.paymentAmount()
            );
        }
    }
}
