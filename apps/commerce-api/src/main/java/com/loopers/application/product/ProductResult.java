package com.loopers.application.product;

import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.stock.StockInfo;
import java.math.BigDecimal;

public record ProductResult(
        Long id,
        String brandName,
        String productName,
        BigDecimal price,
        String status,
        Long quantity,
        Long likeCount,
        boolean isLiked
) {
    public static ProductResult from(ProductInfo productInfo, BrandInfo brandInfo, StockInfo stockInfo, Long likeCount, boolean isLiked) {
        return new ProductResult(
                productInfo.id(),
                brandInfo.name(),
                productInfo.name(),
                productInfo.price(),
                productInfo.status(),
                stockInfo.quantity(),
                likeCount,
                isLiked
        );
    }

    public record Search(
            Long id,
            String brandName,
            String productName,
            BigDecimal price,
            String status,
            Long likeCount,
            boolean isLiked
    ) {
        public static Search from(ProductInfo.Search searchInfo, boolean isLiked) {
            return new Search(
                    searchInfo.id(),
                    searchInfo.brandName(),
                    searchInfo.name(),
                    searchInfo.price(),
                    searchInfo.status(),
                    searchInfo.likeCount(),
                    isLiked
            );
        }
    }
}
