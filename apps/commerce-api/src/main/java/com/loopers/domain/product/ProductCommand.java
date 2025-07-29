package com.loopers.domain.product;

import java.math.BigDecimal;

public class ProductCommand {
    public record Create(
            Long brandId,
            String name,
            BigDecimal price,
            String status
    ) {
    }

    public record Find(Long productId) {
    }
}
