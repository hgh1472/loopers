package com.loopers.domain.like;

import java.util.List;
import java.util.Set;

public interface ProductLikeRepository {

    ProductLike save(ProductLike productLike);

    boolean deleteByProductIdAndUserId(Long productId, Long userId);

    boolean existsByProductIdAndUserId(Long productId, Long userId);

    List<ProductLike> findProductLikesOf(Long userId, Set<Long> productIds);
}
