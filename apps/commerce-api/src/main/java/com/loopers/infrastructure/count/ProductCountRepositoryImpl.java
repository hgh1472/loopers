package com.loopers.infrastructure.count;

import com.loopers.domain.count.ProductCount;
import com.loopers.domain.count.ProductCountRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductCountRepositoryImpl implements ProductCountRepository {

    private final ProductCountJpaRepository productCountJpaRepository;

    @Override
    public ProductCount save(ProductCount productCount) {
        return productCountJpaRepository.save(productCount);
    }

    @Override
    public Optional<ProductCount> findBy(Long productId) {
        return productCountJpaRepository.findByProductId(productId);
    }

    @Override
    public List<ProductCount> findByProductIds(Set<Long> productIds) {
        return productCountJpaRepository.findAllById(productIds);
    }
}
