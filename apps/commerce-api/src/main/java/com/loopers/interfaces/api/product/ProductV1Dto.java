package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductResult;
import java.math.BigDecimal;

public class ProductV1Dto {
    public record ProductResponse(
            Long id,
            String brandName,
            String productName,
            BigDecimal price,
            String status,
            Long quantity,
            Long likeCount,
            boolean isLiked
    ) {
        public static ProductResponse from(ProductResult productResult) {
            return new ProductResponse(
                    productResult.id(),
                    productResult.brandName(),
                    productResult.productName(),
                    productResult.price(),
                    productResult.status(),
                    productResult.quantity(),
                    productResult.likeCount(),
                    productResult.isLiked()
            );
        }
    }
}
