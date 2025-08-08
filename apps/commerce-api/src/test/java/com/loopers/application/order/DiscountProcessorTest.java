package com.loopers.application.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.loopers.domain.coupon.CouponService;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DiscountProcessorTest {
    @InjectMocks
    private DiscountProcessor discountProcessor;
    @Mock
    private CouponService couponService;

    @DisplayName("쿠폰 id가 null인 경우, 할인 금액이 적용되지 않아야 한다.")
    @Test
    void doesNotApplyDiscount_whenCouponIdIsNull() {
        Long couponId = null;
        Long userId = 1L;
        BigDecimal originalAmount = BigDecimal.valueOf(1000);

        BigDecimal paymentAmount = discountProcessor.applyDiscount(couponId, userId, originalAmount);

        assertEquals(originalAmount, paymentAmount);
    }
}
