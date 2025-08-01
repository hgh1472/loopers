package com.loopers.domain.like;

public record ProductLikeActionInfo(
        Long productId,
        Long userId,
        boolean changed
) {
    public static ProductLikeActionInfo of(ProductLike productLike, boolean changed) {
        return new ProductLikeActionInfo(
                productLike.getProductId(),
                productLike.getUserId(),
                changed
        );
    }
}
