package com.loopers.infrastructure.cache;

import com.loopers.domain.cache.ProductCacheRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductCacheRepositoryImpl implements ProductCacheRepository {
    private final RedisTemplate<String, String> masterRedisTemplate;

    @Override
    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "evictFallback")
    public void evictProductCache(String key) {
        masterRedisTemplate.delete(key);
    }

    public void evictFallback(String key, Throwable throwable) {
        log.error("캐시 무효화 실패: key = {}, error = {}", key, throwable.getMessage());
    }
}
