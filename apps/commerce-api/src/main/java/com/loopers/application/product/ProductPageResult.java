package com.loopers.application.product;

import com.loopers.domain.product.ProductSearchInfo;
import java.math.BigDecimal;

public record ProductPageResult(
        Long id,
        String brandName,
        String productName,
        BigDecimal price,
        String status,
        Long likeCount,
        boolean isLiked
) {
    public static ProductPageResult from(ProductSearchInfo productSearchInfo, boolean isLiked) {
        return new ProductPageResult(
                productSearchInfo.id(),
                productSearchInfo.brandName(),
                productSearchInfo.name(),
                productSearchInfo.price(),
                productSearchInfo.status(),
                productSearchInfo.likeCount(),
                isLiked
        );
    }
}
