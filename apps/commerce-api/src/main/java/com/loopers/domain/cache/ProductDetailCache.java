package com.loopers.domain.cache;

import java.math.BigDecimal;

public record ProductDetailCache(
        Long id,
        String brandName,
        String productName,
        BigDecimal price,
        String status,
        Long quantity,
        Long likeCount,
        Long rank
) {
    public static ProductDetailCache from(CacheCommand.ProductDetail command) {
        return new ProductDetailCache(
                command.id(),
                command.brandName(),
                command.productName(),
                command.price(),
                command.status(),
                command.quantity(),
                command.rank(),
                command.likeCount()
        );
    }
}
