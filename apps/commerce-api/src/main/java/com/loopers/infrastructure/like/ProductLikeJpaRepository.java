package com.loopers.infrastructure.like;

import com.loopers.domain.like.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLikeJpaRepository extends JpaRepository<ProductLike, Long> {

    Long countByProductId(Long productId);

    boolean existsByProductIdAndUserId(Long productId, Long userId);
}
