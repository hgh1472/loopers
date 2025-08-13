package com.loopers.infrastructure.product;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCacheReader {

    private final RedisTemplate<String, String> defaultRedisTemplate;

    public Optional<Long> getAllProductCount() {
        return Optional.ofNullable(defaultRedisTemplate.opsForValue().get(RedisKeys.COUNT_ALL.key()))
                .map(Long::parseLong);
    }

    public Optional<Long> getBrandProductCount(Long brandId) {
        return Optional.ofNullable(defaultRedisTemplate.opsForValue().get(String.format(RedisKeys.COUNT_BRAND.key(), brandId)))
                .map(Long::parseLong);
    }
}
