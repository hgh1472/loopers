package com.loopers.application.product;

import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.cache.ProductDetailCache;
import com.loopers.domain.count.ProductCountInfo;
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
    public record ProductDetail(
            Long id,
            String brandName,
            String productName,
            BigDecimal price,
            String status,
            Long quantity,
            Long likeCount
    ) {
        public static ProductDetail from(ProductDetailCache cache) {
            return new ProductDetail(
                    cache.id(),
                    cache.brandName(),
                    cache.productName(),
                    cache.price(),
                    cache.status(),
                    cache.quantity(),
                    cache.likeCount()
            );
        }

        public static ProductDetail from(ProductInfo productInfo, BrandInfo brandInfo, StockInfo stockInfo,
                                         ProductCountInfo productCountInfo) {
            return new ProductDetail(
                    productInfo.id(),
                    brandInfo.name(),
                    productInfo.name(),
                    productInfo.price(),
                    productInfo.status(),
                    stockInfo.quantity(),
                    productCountInfo.likeCount()
            );
        }
    }

    public record ProductUserDetail(
            boolean isLiked
    ) {
        public static ProductUserDetail from(Boolean isLiked) {
            return new ProductUserDetail(isLiked);
        }
    }

    public static ProductResult from(ProductDetail productDetail, ProductUserDetail userDetail) {
        return new ProductResult(
                productDetail.id(),
                productDetail.brandName(),
                productDetail.productName(),
                productDetail.price(),
                productDetail.status(),
                productDetail.quantity(),
                productDetail.likeCount(),
                userDetail.isLiked()
        );
    }

    public static ProductResult from(ProductInfo productInfo, BrandInfo brandInfo, StockInfo stockInfo, Long likeCount,
                                     boolean isLiked) {
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

    public record Card(
            Long id,
            String brandName,
            String productName,
            BigDecimal price,
            String status,
            Long likeCount,
            boolean isLiked
    ) {
        public static Card from(ProductInfo.Search searchInfo, boolean isLiked) {
            return new Card(
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
