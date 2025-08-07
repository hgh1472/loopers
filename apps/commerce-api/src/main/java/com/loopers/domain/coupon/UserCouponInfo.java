package com.loopers.domain.coupon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserCouponInfo(
        Long id,
        Long couponId,
        Long userId,
        DiscountPolicy discountPolicy,
        LocalDateTime usedAt,
        LocalDateTime expiredAt
) {
    public record Use(
            Long id,
            BigDecimal originalAmount,
            BigDecimal paymentAmount
    ) {
    }

    public static UserCouponInfo from(UserCoupon userCoupon) {
        return new UserCouponInfo(
                userCoupon.getId(),
                userCoupon.getCouponId(),
                userCoupon.getUserId(),
                userCoupon.getDiscountPolicy(),
                userCoupon.getUsedAt(),
                userCoupon.getExpiredAt()
        );
    }
}
