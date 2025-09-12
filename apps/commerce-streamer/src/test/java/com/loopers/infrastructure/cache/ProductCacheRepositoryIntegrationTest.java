package com.loopers.infrastructure.cache;

import static org.mockito.BDDMockito.*;

import com.loopers.domain.cache.ProductCacheRepository;
import com.loopers.utils.RedisCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
class ProductCacheRepositoryIntegrationTest {
    @MockitoSpyBean
    private ProductCacheRepository productCacheRepository;
    @MockitoBean
    private RedisTemplate<String, Object> masterRedisTemplate;
    @Autowired
    private RedisCleanUp redisCleanUp;

    @AfterEach
    void tearDown() {
        redisCleanUp.truncateAll();
    }

    @Nested
    @DisplayName("캐시 무효화 시,")
    class Evict {
        @Test
        @DisplayName("레디스 커넥션 예외가 발생한다면, 폴백 메서드로 대체된다.")
        void evictFallback_whenRedisConnectionFailureException() {
            String key = "key";
            given(masterRedisTemplate.delete(key))
                    .willThrow(new RedisConnectionFailureException("Redis connection failed"));

            productCacheRepository.evictProductCache(key);

            verify(productCacheRepository, times(1)).evictProductCache(key);
        }
    }
}
