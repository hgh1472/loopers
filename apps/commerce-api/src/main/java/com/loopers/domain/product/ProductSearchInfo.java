package com.loopers.domain.product;

import java.math.BigDecimal;

public record ProductSearchInfo(
        Long id,
        Long brandId,
        String brandName,
        String name,
        BigDecimal price,
        String status,
        Long likeCount
) {
    public static ProductSearchInfo from(ProductSearchView view) {
        return new ProductSearchInfo(
                view.id(),
                view.brandId(),
                view.brandName(),
                view.name(),
                view.price().getValue(),
                view.status().name(),
                view.likeCount()
        );
    }
}
