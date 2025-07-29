package com.loopers.infrastructure.like;

import com.loopers.domain.like.ProductLike;
import com.loopers.domain.like.ProductLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductLikeRepositoryImpl implements ProductLikeRepository {

    private final ProductLikeJpaRepository productLikeJpaRepository;

    @Override
    public ProductLike save(ProductLike productLike) {
        return productLikeJpaRepository.save(productLike);
    }

    @Override
    public void deleteByProductIdAndUserId(Long productId, Long userId) {
        productLikeJpaRepository.deleteByProductIdAndUserId(productId, userId);
    }

    @Override
    public Long countByProductId(Long productId) {
        return productLikeJpaRepository.countByProductId(productId);
    }

    @Override
    public boolean existsByProductIdAndUserId(Long productId, Long userId) {
        return productLikeJpaRepository.existsByProductIdAndUserId(productId, userId);
    }
}
