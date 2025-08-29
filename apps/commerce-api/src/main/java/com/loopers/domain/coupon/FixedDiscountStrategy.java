package com.loopers.domain.coupon;

import java.math.BigDecimal;

public class FixedDiscountStrategy implements DiscountStrategy {

    private final BigDecimal amount;

    public FixedDiscountStrategy(DiscountPolicy discountPolicy) {
        this.amount = discountPolicy.getValue();
    }

    @Override
    public BigDecimal discount(BigDecimal amount) {
        return this.amount;
    }
}
