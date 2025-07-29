package com.loopers.interfaces.api.like;

import com.loopers.domain.like.ProductLikeInfo;

public class LikeV1Dto {
    public record ProductLikeResponse(
            Long productId,
            Long userId
    ) {
        public static ProductLikeResponse from(ProductLikeInfo productLikeInfo) {
            return new ProductLikeResponse(
                    productLikeInfo.productId(),
                    productLikeInfo.userId()
            );
        }
    }
}
