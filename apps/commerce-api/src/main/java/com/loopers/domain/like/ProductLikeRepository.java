package com.loopers.domain.like;

public interface ProductLikeRepository {

    ProductLike save(ProductLike productLike);

    Long countByProductId(Long productId);

    boolean existsByProductIdAndUserId(Long productId, Long userId);
}
