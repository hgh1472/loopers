package com.loopers.infrastructure.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.cache.ProductDetailCache;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductDetailCacheWriter {

    private final RedisTemplate<String, String> masterRedisTemplate;
    private final ObjectMapper objectMapper;

    public void writeProductCard(ProductDetailCache productDetail, String key, Duration ttl) throws JsonProcessingException {
        String value = objectMapper.writeValueAsString(productDetail);
        masterRedisTemplate.opsForValue().set(key, value, ttl);
    }

    public void evictProductCard(String key) {
        masterRedisTemplate.delete(key);
    }
}
