package com.loopers.application.like;

import com.loopers.domain.count.ProductCountInfo;
import com.loopers.domain.like.ProductLikeActionInfo;
import com.loopers.domain.product.ProductSearchInfo;
import java.math.BigDecimal;

public class LikeResult {
    public record Product(
            Long userId,
            Long productId
    ) {
        public static LikeResult.Product from(ProductLikeActionInfo productLikeActionInfo) {
            return new LikeResult.Product(
                    productLikeActionInfo.userId(),
                    productLikeActionInfo.productId()
            );
        }
    }

    public record ProductList(
            Long productId,
            String brandName,
            String productName,
            BigDecimal price,
            String status,
            Long likeCount,
            boolean isLiked
    ) {
        public static LikeResult.ProductList from(ProductCountInfo productCountInfo,
                                                  ProductSearchInfo productSearchInfo,
                                                  boolean isLiked) {
            return new LikeResult.ProductList(
                    productSearchInfo.id(),
                    productSearchInfo.brandName(),
                    productSearchInfo.name(),
                    productSearchInfo.price(),
                    productSearchInfo.status(),
                    productCountInfo.likeCount(),
                    isLiked
            );
        }
    }
}
