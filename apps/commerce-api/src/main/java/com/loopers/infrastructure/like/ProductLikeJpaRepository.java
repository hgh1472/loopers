package com.loopers.infrastructure.like;

import com.loopers.domain.like.ProductLike;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductLikeJpaRepository extends JpaRepository<ProductLike, Long> {

    int deleteByProductIdAndUserId(Long productId, Long userId);

    boolean existsByProductIdAndUserId(Long productId, Long userId);

    List<ProductLike> findProductLikesByUserIdAndProductIdIn(Long userId, Set<Long> productIds);

    @Query("SELECT pl.productId FROM ProductLike pl WHERE pl.userId = :userId AND pl.productId IN :productIds")
    Set<Long> findLikedProductIdsByUserIdAndProductIdIn(Long userId, Set<Long> productIds);

    List<ProductLike> findAllByUserId(Long userId);
}
