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

    public record Search(
            Long id,
            Long brandId,
            String brandName,
            String name,
            BigDecimal price,
            String status,
            Long likeCount
    ) {
        public static Search from(ProductSearchView view) {
            return new Search(
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
}
