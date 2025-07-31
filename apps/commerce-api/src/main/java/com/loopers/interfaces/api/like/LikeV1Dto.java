package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeResult;

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
}
