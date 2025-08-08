package com.loopers.domain.coupon;

import java.math.BigDecimal;

public record CouponInfo(
        Long id,
        String name,
        DiscountPolicy discountPolicy,
        BigDecimal minimumOrderAmount,
        Integer expireHours,
        Long remainingQuantity,
        Long issuedQuantity
) {
    public static CouponInfo from(Coupon coupon) {
        return new CouponInfo(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountPolicy(),
                coupon.getMinimumOrderAmount(),
                coupon.getExpireHours(),
                coupon.getRemainingQuantity(),
                coupon.getIssuedQuantity()
        );
    }
}
