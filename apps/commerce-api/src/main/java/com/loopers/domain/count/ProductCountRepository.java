package com.loopers.domain.count;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductCountRepository {

    ProductCount save(ProductCount productCount);

    Optional<ProductCount> findBy(Long productId);

    List<ProductCount> findByProductIds(Set<Long> productIds);
}
