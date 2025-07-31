package com.loopers.infrastructure.count;

import com.loopers.domain.count.ProductCount;
import com.loopers.domain.count.ProductCountRepository;
import java.util.Optional;
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
}
