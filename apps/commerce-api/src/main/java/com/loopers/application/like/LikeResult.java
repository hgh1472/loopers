package com.loopers.application.like;

import com.loopers.domain.like.ProductLikeInfo;

public class LikeResult {
    public record Product(
            Long userId,
            Long productId
    ) {
        public static LikeResult.Product from(ProductLikeInfo productLikeInfo) {
            return new LikeResult.Product(
                    productLikeInfo.userId(),
                    productLikeInfo.productId()
            );
        }
    }
}
