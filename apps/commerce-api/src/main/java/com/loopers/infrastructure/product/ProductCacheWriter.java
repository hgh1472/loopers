package com.loopers.infrastructure.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCacheWriter {

    private final RedisTemplate<String, String> masterRedisTemplate;

    public void writeAllProductCount(Long count) {
        masterRedisTemplate.opsForValue().set(RedisKeys.COUNT_ALL.key(), count.toString(), RedisKeys.COUNT_ALL.ttl());
    }

    public void writeBrandProductCount(Long brandId, Long count) {
        masterRedisTemplate.opsForValue().set(String.format(RedisKeys.COUNT_BRAND.key(), brandId), count.toString(), RedisKeys.COUNT_BRAND.ttl());
    }
}
