package com.loopers.domain.product;

import java.math.BigDecimal;

public record ProductInfo(
        Long id,
        Long brandId,
        String name,
        BigDecimal price,
        String status
) {
    public static ProductInfo from(Product product) {
        return new ProductInfo(
                product.getId(),
                product.getBrandId(),
                product.getName(),
                product.getPrice().getValue(),
                product.getStatus().name()
        );
    }
}
