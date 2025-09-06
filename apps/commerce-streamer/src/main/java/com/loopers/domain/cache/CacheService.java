package com.loopers.domain.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheService {
    private final ProductCacheRepository productCacheRepository;

    public void evictProductCache(CacheCommand.EvictProduct cmd) {
        String key = CacheKeys.PRODUCT_DETAIL.key().formatted(cmd.productId());
        productCacheRepository.evictProductCache(key);
    }
}
