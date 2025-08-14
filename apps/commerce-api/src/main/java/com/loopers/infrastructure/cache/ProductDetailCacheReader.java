package com.loopers.infrastructure.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.cache.ProductDetailCache;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductDetailCacheReader {
    private final RedisTemplate<String, String> defaultRedisTemplate;
    private final ObjectMapper objectMapper;

    public Optional<ProductDetailCache> readProductCard(String key) throws JsonProcessingException {
        String value = defaultRedisTemplate.opsForValue().get(key);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(objectMapper.readValue(value, ProductDetailCache.class));
    }
}
