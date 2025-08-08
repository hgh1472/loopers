package com.loopers.domain.coupon;

import org.springframework.stereotype.Component;

@Component
public class DiscountPolicyAdapter {

    public DiscountPolicyAdapter() {
    }

    public DiscountStrategy from(DiscountPolicy discountPolicy) {
        return switch (discountPolicy.getType()) {
            case FIXED -> new FixedDiscountStrategy(discountPolicy);
            case RATE -> new RateDiscountStrategy(discountPolicy);
        };
    }
}
