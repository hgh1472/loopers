package com.loopers.domain.product;

import java.math.BigDecimal;
import java.util.Set;

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

    public record Page(
            Long brandId,
            Integer page,
            Integer size,
            String sort
    ) {
    }

    public record Search(
            Set<Long> productIds
    ) {
    }
}
