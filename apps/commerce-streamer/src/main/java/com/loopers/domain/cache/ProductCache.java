package com.loopers.domain.cache;

import java.math.BigDecimal;

public record ProductCache(
        Long id,
        String brandName,
        String productName,
        BigDecimal price,
        String status,
        Long quantity,
        Long likeCount
) {
}
