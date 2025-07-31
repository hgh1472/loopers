package com.loopers.domain.product;

public record ProductSearchView(
        Long id,
        Long brandId,
        String brandName,
        String name,
        Price price,
        Product.ProductStatus status,
        Long likeCount
) {
}
