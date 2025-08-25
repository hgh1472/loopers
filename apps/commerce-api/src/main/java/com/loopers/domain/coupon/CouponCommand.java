package com.loopers.domain.coupon;

import java.math.BigDecimal;

public class CouponCommand {
    public record Create(
            String name,
            DiscountPolicy discountPolicy,
            BigDecimal minimumOrderAmount,
            Integer expireHours,
            Long remainingQuantity
    ) {
    }

    public record Issue(Long couponId, Long userId) {
    }

    public record Use(Long couponId, Long userId, BigDecimal originalAmount) {
    }

    public record Restore(Long couponId, Long userId) {
    }
}
