package com.loopers.application.ranking;

import java.math.BigDecimal;

public record RankingResult(
        Long productId,
        String brandName,
        String productName,
        BigDecimal price,
        String status,
        Long rank
) {
}
