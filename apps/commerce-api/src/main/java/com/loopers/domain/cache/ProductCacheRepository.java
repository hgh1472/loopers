package com.loopers.domain.cache;

import java.time.Duration;
import java.util.Optional;

public interface ProductCacheRepository {

    Optional<ProductDetailCache> findProductDetail(String key);

    void writeProductDetail(ProductDetailCache productDetailCache, String key, Duration ttl);
}
