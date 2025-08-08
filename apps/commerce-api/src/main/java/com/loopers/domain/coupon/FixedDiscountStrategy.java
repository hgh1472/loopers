package com.loopers.domain.coupon;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FixedDiscountStrategy implements DiscountStrategy {

    private final BigDecimal amount;

    public FixedDiscountStrategy(DiscountPolicy discountPolicy) {
        this.amount = discountPolicy.getValue();
    }

    @Override
    public BigDecimal discount(BigDecimal amount) {
        return amount.subtract(this.amount).setScale(0, RoundingMode.FLOOR).max(BigDecimal.ZERO);
    }
}
