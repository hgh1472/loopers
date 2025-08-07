package com.loopers.application.order;

import com.loopers.domain.coupon.CouponCommand;
import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.coupon.UserCouponInfo;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiscountProcessor {

    private final CouponService couponService;

    public BigDecimal applyDiscount(Long couponId, Long userId, BigDecimal originalAmount) {
        BigDecimal paymentAmount = originalAmount;

        if (couponId != null) {
            UserCouponInfo.Use useInfo = couponService.use(new CouponCommand.Use(couponId, userId, originalAmount));
            paymentAmount = useInfo.paymentAmount();
        }
        return paymentAmount;
    }
}
