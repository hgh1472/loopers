package com.loopers.domain.cache;

import java.time.Duration;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CacheKeys {

    PRODUCT_DETAIL("product-detail:%d", Duration.ofSeconds(5));

    private final String key;
    private final Duration ttl;

    public String key() {
        return key;
    }

    public Duration ttl() {
        return ttl;
    }
}
