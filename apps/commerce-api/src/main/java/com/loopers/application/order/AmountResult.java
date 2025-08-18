package com.loopers.application.order;

import java.math.BigDecimal;

public record AmountResult(
        BigDecimal originalAmount,
        BigDecimal discountAmount,
        Long pointAmount
) {
}
