package com.loopers.infrastructure.like;

import com.loopers.domain.like.ProductLike;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLikeJpaRepository extends JpaRepository<ProductLike, Long> {

    int deleteByProductIdAndUserId(Long productId, Long userId);

    boolean existsByProductIdAndUserId(Long productId, Long userId);

    List<ProductLike> findProductLikesByUserIdAndProductIdIn(Long userId, Set<Long> productIds);

    List<ProductLike> findAllByUserId(Long userId);
}
