package com.loopers.domain.coupon;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RateDiscountStrategy implements DiscountStrategy {

    private final BigDecimal rate;

    public RateDiscountStrategy(DiscountPolicy discountPolicy) {
        this.rate = discountPolicy.getValue();
    }

    @Override
    public BigDecimal discount(BigDecimal amount) {
        BigDecimal discount = amount.multiply(rate);
        return amount.subtract(discount).setScale(0, RoundingMode.FLOOR);
    }
}
