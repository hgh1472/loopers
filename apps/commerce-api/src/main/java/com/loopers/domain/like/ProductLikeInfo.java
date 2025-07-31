package com.loopers.domain.like;

public record ProductLikeInfo(
        Long productId,
        Long userId,
        boolean changed
) {
    public static ProductLikeInfo of(ProductLike productLike, boolean changed) {
        return new ProductLikeInfo(
                productLike.getProductId(),
                productLike.getUserId(),
                changed
        );
    }
}
