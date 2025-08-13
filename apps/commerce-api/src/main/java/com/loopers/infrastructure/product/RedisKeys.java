package com.loopers.infrastructure.product;

import java.time.Duration;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RedisKeys {
    COUNT_ALL("product:count:all", Duration.ofMinutes(6)),
    COUNT_BRAND("product:count:brand:%d", Duration.ofMinutes(1));

    private final String key;
    private final Duration ttl;

    public String key() {
        return key;
    }

    public Duration ttl() {
        return ttl;
    }
}
