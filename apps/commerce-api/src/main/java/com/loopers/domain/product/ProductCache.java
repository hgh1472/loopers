package com.loopers.domain.product;

import java.util.Optional;

public interface ProductCache {

    void writeProductCount(Long brandId, Long count);

    void writeAllProductCount(Long count);

    Optional<Long> readProductCount(Long brandId);

    Optional<Long> readAllProductCount();
}
