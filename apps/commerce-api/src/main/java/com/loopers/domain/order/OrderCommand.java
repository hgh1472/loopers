package com.loopers.domain.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderCommand {
    public record Order(
            Long userId,
            Long couponId,
            List<Line> lines,
            Delivery delivery,
            BigDecimal originalAmount,
            BigDecimal discountAmount,
            Long pointAmount
    ) {
    }

    public record Line(
            Long productId,
            Long quantity,
            BigDecimal amount
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

    public record Get(UUID orderId) {
    }

    public record GetOrders(Long userId) {
    }

    public record Paid(UUID orderId) {
    }

    public record Fail(UUID orderId, Reason reason) {

        public enum Reason {
            OUT_OF_STOCK("재고 부족"),
            POINT_EXHAUSTED("포인트 부족"),
            PAYMENT_FAILED("결제 실패"),
            INTERNAL_ERROR("내부 오류");

            private final String description;

            Reason(String description) {
                this.description = description;
            }

            public String getDescription() {
                return description;
            }

            public static Reason from(String description) {
                if (description.equals("재고가 부족합니다.")) {
                    return OUT_OF_STOCK;
                } else if (description.equals("포인트가 부족합니다.")) {
                    return POINT_EXHAUSTED;
                }
                return INTERNAL_ERROR;
            }
        }
    }
}
