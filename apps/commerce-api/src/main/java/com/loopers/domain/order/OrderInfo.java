package com.loopers.domain.order;

import java.math.BigDecimal;
import java.util.List;

public record OrderInfo(
        Long id,
        Long userId,
        String orderStatus,
        List<OrderLineInfo> orderLineInfos,
        OrderDeliveryInfo orderDeliveryInfo,
        OrderPaymentInfo orderPaymentInfo
) {
    public static OrderInfo from(Order order) {
        return new OrderInfo(
                order.getId(),
                order.getUserId(),
                order.getStatus().name(),
                order.getOrderLines().stream()
                        .map(OrderLineInfo::from)
                        .toList(),
                OrderDeliveryInfo.from(order.getOrderDelivery()),
                OrderPaymentInfo.from(order.getOrderPayment())
        );
    }

    public record OrderLineInfo(
            Long productId,
            Long quantity,
            BigDecimal amount
    ) {
        public static OrderLineInfo from(OrderLine orderLine) {
            return new OrderLineInfo(
                    orderLine.getProductId(),
                    orderLine.getQuantity(),
                    orderLine.getAmount()
            );
        }
    }

    public record OrderDeliveryInfo(
            String receiverName,
            String phoneNumber,
            String baseAddress,
            String detailAddress,
            String requirements
    ) {
        public static OrderDeliveryInfo from(OrderDelivery orderDelivery) {
            return new OrderDeliveryInfo(
                    orderDelivery.getReceiverName(),
                    orderDelivery.getPhoneNumber(),
                    orderDelivery.getBaseAddress(),
                    orderDelivery.getDetailAddress(),
                    orderDelivery.getRequirements()
            );
        }
    }

    public record OrderPaymentInfo(
            BigDecimal paymentAmount
    ) {
        public static OrderPaymentInfo from(OrderPayment orderPayment) {
            return new OrderPaymentInfo(
                    orderPayment.getPaymentAmount()
            );
        }
    }
}
