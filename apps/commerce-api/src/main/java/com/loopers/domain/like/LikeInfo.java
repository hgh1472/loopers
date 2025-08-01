package com.loopers.domain.like;

public class LikeInfo {
    public record Product(
            Long productId,
            Long userId
    ) {
        public static LikeInfo.Product from(ProductLike productLike) {
            return new LikeInfo.Product(productLike.getProductId(), productLike.getUserId());
        }
    }

    public record ProductAction(
            Long productId,
            Long userId,
            boolean changed
    ) {
        public static ProductAction of(ProductLike productLike, boolean changed) {
            return new ProductAction(
                    productLike.getProductId(),
                    productLike.getUserId(),
                    changed
            );
        }
    }

    public record IsLiked(
            boolean liked
    ) {
    }

    public record ProductState(
            Long productId,
            Long userId,
            boolean isLiked
    ) {
    }
}
