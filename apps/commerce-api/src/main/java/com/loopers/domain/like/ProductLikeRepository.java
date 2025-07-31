package com.loopers.domain.like;

public interface ProductLikeRepository {

    ProductLike save(ProductLike productLike);

    boolean deleteByProductIdAndUserId(Long productId, Long userId);

    Long countByProductId(Long productId);

    boolean existsByProductIdAndUserId(Long productId, Long userId);
}
