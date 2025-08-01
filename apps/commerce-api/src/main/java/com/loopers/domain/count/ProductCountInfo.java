package com.loopers.domain.count;

public record ProductCountInfo(
        Long productId,
        Long likeCount
) {
    public static ProductCountInfo from(ProductCount productCount) {
        return new ProductCountInfo(productCount.getProductId(), productCount.getLikeCount());
    }
}
