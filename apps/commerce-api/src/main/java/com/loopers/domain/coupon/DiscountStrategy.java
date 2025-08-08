package com.loopers.domain.coupon;

import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal discount(BigDecimal amount);
}
