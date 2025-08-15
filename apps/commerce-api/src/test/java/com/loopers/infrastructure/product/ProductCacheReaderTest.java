package com.loopers.infrastructure.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.utils.RedisCleanUp;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class ProductCacheReaderTest {
    @Autowired
    private ProductCacheReader productCacheReader;
    @Autowired
    private RedisTemplate<String, String> masterRedisTemplate;
    @Autowired
    private RedisCleanUp redisCleanUp;

    @AfterEach
    void tearDown() {
        redisCleanUp.truncateAll();
    }

    @Test
    @DisplayName("전체 상품 개수에 대한 캐시가 존재할 경우, 캐시된 상품 개수를 반환한다.")
    void returnProductCount_whenCacheExists() {
        Long count = 1000L;
        masterRedisTemplate.opsForValue().set(RedisKeys.COUNT_ALL.key(), count.toString(), RedisKeys.COUNT_ALL.ttl());

        Optional<Long> cached = productCacheReader.getAllProductCount();

        assertThat(cached).isPresent();
        assertThat(cached.get()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("전체 상품 개수에 대한 캐시가 존재하지 않는 경우, Optional.empty를 반환한다.")
    void returnEmpty_whenCacheNotExists() {
        Optional<Long> cached = productCacheReader.getAllProductCount();

        assertThat(cached).isEmpty();
    }

    @Test
    @DisplayName("브랜드별 상품 개수에 대한 캐시가 존재할 경우, 캐시된 상품 개수를 반환한다.")
    void returnBrandProductCount_whenCacheExists() {
        Long brandId = 1L;
        Long count = 500L;
        masterRedisTemplate.opsForValue().set(String.format(RedisKeys.COUNT_BRAND.key(), brandId), count.toString(), RedisKeys.COUNT_BRAND.ttl());

        Optional<Long> cached = productCacheReader.getBrandProductCount(brandId);

        assertThat(cached).isPresent();
        assertThat(cached.get()).isEqualTo(500L);
    }

    @Test
    @DisplayName("브랜드별 상품 개수에 대한 캐시가 존재하지 않는 경우, Optional.empty를 반환한다.")
    void returnEmpty_whenBrandCacheNotExists() {
        Long brandId = 1L;

        Optional<Long> cached = productCacheReader.getBrandProductCount(brandId);

        assertThat(cached).isEmpty();
    }
}
