package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderCriteria;
import com.loopers.application.order.OrderResult;
import java.math.BigDecimal;
import java.util.List;

public class OrderV1Dto {
    public record OrderRequest(
            List<Line> lines,
            Delivery delivery,
            Long couponId
    ) {
    }

    public record Line(
            Long productId,
            Long quantity
    ) {
        public OrderCriteria.Line toCriteriaLine() {
            return new OrderCriteria.Line(productId, quantity);
        }
    }

    public record Delivery(
            String receiverName,
            String phoneNumber,
            String baseAddress,
            String detailAddress,
            String requirements
    ) {
        public OrderCriteria.Delivery toCriteriaDelivery() {
            return new OrderCriteria.Delivery(
                    receiverName,
                    phoneNumber,
                    baseAddress,
                    detailAddress,
                    requirements
            );
        }
    }

    public record OrderResponse(
            Long orderId,
            List<Line> lines,
            Delivery delivery,
            Payment payment
    ) {
        public static OrderResponse from(OrderResult orderResult) {
            List<Line> lines = orderResult.lines().stream()
                    .map(line -> new Line(line.productId(), line.quantity()))
                    .toList();
            Delivery delivery = new Delivery(
                    orderResult.delivery().receiverName(),
                    orderResult.delivery().phoneNumber(),
                    orderResult.delivery().baseAddress(),
                    orderResult.delivery().detailAddress(),
                    orderResult.delivery().requirements()
            );
            Payment payment = new Payment(orderResult.payment().originalAmount(), orderResult.payment().paymentAmount());
            return new OrderResponse(orderResult.id(), lines, delivery, payment);
        }
    }

    public record Payment(
            BigDecimal originalAmount,
            BigDecimal paymentAmount
    ) {

    }
}
