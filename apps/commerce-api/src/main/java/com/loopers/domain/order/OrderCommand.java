package com.loopers.domain.order;

import java.math.BigDecimal;
import java.util.List;

public class OrderCommand {
    public record Order(
            Long userId,
            List<Line> lines,
            Delivery delivery
    ) {
    }
    public record Line(
            Long productId,
            Long quantity,
            BigDecimal unitPrice
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

    public record Get(Long orderId) {
    }

    public record GetOrders(Long userId) {
    }
}
