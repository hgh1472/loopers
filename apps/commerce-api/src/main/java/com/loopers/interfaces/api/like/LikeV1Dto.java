package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeResult;
import com.loopers.application.like.LikeResult.ProductCard;
import java.math.BigDecimal;

public class LikeV1Dto {
    public record ProductLikeResponse(
            Long productId,
            Long userId
    ) {
        public static ProductLikeResponse from(LikeResult.Product result) {
            return new ProductLikeResponse(
                    result.productId(),
                    result.userId()
            );
        }
    }

    public record LikedProductResponse(
            Long productId,
            String brandName,
            String productName,
            BigDecimal price,
            String status,
            Long likeCount,
            boolean isLiked
    ) {
        public static LikedProductResponse from(ProductCard result) {
            return new LikedProductResponse(
                    result.productId(),
                    result.brandName(),
                    result.productName(),
                    result.price(),
                    result.status(),
                    result.likeCount(),
                    result.isLiked()
            );
        }
    }
}
