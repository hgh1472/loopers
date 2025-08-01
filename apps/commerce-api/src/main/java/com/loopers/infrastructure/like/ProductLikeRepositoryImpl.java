package com.loopers.infrastructure.like;

import com.loopers.domain.like.ProductLike;
import com.loopers.domain.like.ProductLikeRepository;
import java.util.List;
import java.util.Set;
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
    public boolean deleteByProductIdAndUserId(Long productId, Long userId) {
        int deletedRow = productLikeJpaRepository.deleteByProductIdAndUserId(productId, userId);
        return deletedRow > 0;
    }

    @Override
    public boolean existsByProductIdAndUserId(Long productId, Long userId) {
        return productLikeJpaRepository.existsByProductIdAndUserId(productId, userId);
    }

    @Override
    public List<ProductLike> findProductLikesOf(Long userId, Set<Long> productIds) {
        return productLikeJpaRepository.findProductLikesByUserIdAndProductIdIn(userId, productIds);
    }

    @Override
    public List<ProductLike> findAllByUserId(Long userId) {
        return productLikeJpaRepository.findAllByUserId(userId);
    }
}
