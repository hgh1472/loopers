package com.loopers.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RateDiscountPolicyTest {

    @DisplayName("정률 할인 적용 시, 올바른 할인 금액을 반환한다.")
    @Test
    void returnCorrectDiscountAmount() {
        DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("0.1"), DiscountPolicy.Type.RATE);
        RateDiscountStrategy rateDiscountPolicy = new RateDiscountStrategy(discountPolicy);
        BigDecimal amount = new BigDecimal("1000");

        BigDecimal paymentAmount = rateDiscountPolicy.discount(amount);

        assertThat(paymentAmount).isEqualTo(new BigDecimal("100"));
    }
}
