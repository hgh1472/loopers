package com.loopers.domain.cache;

import static org.junit.jupiter.api.Assertions.*;

import com.loopers.utils.RedisCleanUp;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class CacheServiceIntegrationTest {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private RedisCleanUp redisCleanUp;
    @Autowired
    private RedisTemplate<String, String> masterRedisTemplate;

    @AfterEach
    void tearDown() {
        redisCleanUp.truncateAll();
    }

    @Nested
    @DisplayName("상품 정보 변경 시,")
    class EvictProductCache {
        @Test
        @DisplayName("상품 상세 Redis 캐시를 삭제한다.")
        void evictProductCache() {
            ProductCache productCache = new ProductCache(1L, "Brand", "Product", BigDecimal.valueOf(1000), "ON_SALE", 1L, 1L);
            masterRedisTemplate.opsForValue().set(CacheKeys.PRODUCT_DETAIL.key().formatted(1L), productCache.toString());

            cacheService.evictProductCache(new CacheCommand.EvictProduct(1L));

            assertNull(masterRedisTemplate.opsForValue().get(CacheKeys.PRODUCT_DETAIL.key().formatted(1L)));
        }
    }
}
