package com.loopers.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FixedDiscountPolicyTest {

    @DisplayName("정액 할인 적용 시, 할인 금액이 올바르게 계산되어야 한다.")
    @Test
    void returnsCorrectDiscountAmount() {
        DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000"), DiscountPolicy.Type.FIXED);
        FixedDiscountStrategy fixedDiscountPolicy = new FixedDiscountStrategy(discountPolicy);
        BigDecimal originalAmount = new BigDecimal("5000");

        BigDecimal paymentAmount = fixedDiscountPolicy.discount(originalAmount);

        assertThat(paymentAmount).isEqualTo(new BigDecimal("4000"));
    }

}
