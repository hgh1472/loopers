package com.loopers.application.order;

import com.loopers.domain.order.OrderInfo;
import java.math.BigDecimal;
import java.util.List;

public record OrderResult(
        Long id,
        String orderStatus,
        List<OrderLineResult> orderLineResults,
        OrderDeliveryResult orderDeliveryResult,
        OrderPaymentResult orderPaymentResult
) {
    public static OrderResult from(OrderInfo orderInfo) {
        return new OrderResult(
                orderInfo.id(),
                orderInfo.orderStatus(),
                orderInfo.orderLineInfos().stream()
                        .map(OrderLineResult::from)
                        .toList(),
                OrderDeliveryResult.from(orderInfo.orderDeliveryInfo()),
                OrderPaymentResult.from(orderInfo.orderPaymentInfo())
        );
    }

    public record OrderLineResult(
            Long productId,
            Long quantity,
            BigDecimal amount
    ) {
        public static OrderLineResult from(OrderInfo.OrderLineInfo orderLineInfo) {
            return new OrderLineResult(
                    orderLineInfo.productId(),
                    orderLineInfo.quantity(),
                    orderLineInfo.amount()
            );
        }
    }

    public record OrderDeliveryResult(
            String receiverName,
            String phoneNumber,
            String baseAddress,
            String detailAddress,
            String requirements
    ) {
        public static OrderDeliveryResult from(OrderInfo.OrderDeliveryInfo orderDeliveryInfo) {
            return new OrderDeliveryResult(
                    orderDeliveryInfo.receiverName(),
                    orderDeliveryInfo.phoneNumber(),
                    orderDeliveryInfo.baseAddress(),
                    orderDeliveryInfo.detailAddress(),
                    orderDeliveryInfo.requirements()
            );
        }
    }

    public record OrderPaymentResult(
            BigDecimal paymentAmount
    ) {
        public static OrderPaymentResult from(OrderInfo.OrderPaymentInfo orderPaymentInfo) {
            return new OrderPaymentResult(
                    orderPaymentInfo.paymentAmount()
            );
        }
    }
}
