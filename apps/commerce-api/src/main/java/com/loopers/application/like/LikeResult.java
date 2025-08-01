package com.loopers.application.like;

import com.loopers.domain.count.ProductCountInfo;
import com.loopers.domain.like.LikeInfo;
import com.loopers.domain.product.ProductInfo;
import java.math.BigDecimal;

public class LikeResult {
    public record Product(
            Long userId,
            Long productId
    ) {
        public static LikeResult.Product from(LikeInfo.ProductAction actionInfo) {
            return new LikeResult.Product(
                    actionInfo.userId(),
                    actionInfo.productId()
            );
        }
    }

    public record ProductCard(
            Long productId,
            String brandName,
            String productName,
            BigDecimal price,
            String status,
            Long likeCount,
            boolean isLiked
    ) {
        public static ProductCard from(ProductCountInfo productCountInfo,
                                       ProductInfo.Search searchInfo,
                                       boolean isLiked) {
            return new ProductCard(
                    searchInfo.id(),
                    searchInfo.brandName(),
                    searchInfo.name(),
                    searchInfo.price(),
                    searchInfo.status(),
                    productCountInfo.likeCount(),
                    isLiked
            );
        }
    }
}
