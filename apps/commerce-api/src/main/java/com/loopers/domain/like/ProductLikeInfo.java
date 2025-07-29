package com.loopers.domain.like;

public record ProductLikeInfo(
        Long productId,
        Long userId
) {
    public static ProductLikeInfo from(ProductLike productLike) {
        return new ProductLikeInfo(
                productLike.getProductId(),
                productLike.getUserId()
        );
    }
}
