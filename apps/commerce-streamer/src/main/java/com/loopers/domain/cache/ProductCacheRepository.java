package com.loopers.domain.cache;

public interface ProductCacheRepository {
    void evictProductCache(String key);
}
