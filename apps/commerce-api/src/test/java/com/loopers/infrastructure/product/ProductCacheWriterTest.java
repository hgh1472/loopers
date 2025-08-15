package com.loopers.infrastructure.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class ProductCacheWriterTest {

    @Autowired
    private ProductCacheWriter productCacheWriter;
    @Autowired
    private RedisTemplate<String, String> defaultRedisTemplate;

    @Test
    @DisplayName("상품 전체 개수에 대한 캐시를 쓴다.")
    void writeAllProductCount() {
        productCacheWriter.writeAllProductCount(100L);

        Optional<Long> count = Optional.ofNullable(defaultRedisTemplate.opsForValue().get(RedisKeys.COUNT_ALL.key()))
                .map(Long::parseLong);
        assertThat(count).isPresent();
        assertThat(count.get()).isEqualTo(100L);
    }

    @Test
    @DisplayName("특정 브랜드 상품 개수에 대한 캐시를 쓴다.")
    void writeBrandProductCount() {
        Long brandId = 1L;
        productCacheWriter.writeBrandProductCount(brandId, 50L);

        Optional<Long> count = Optional.ofNullable(defaultRedisTemplate.opsForValue().get(String.format(RedisKeys.COUNT_BRAND.key(), brandId)))
                .map(Long::parseLong);
        assertThat(count).isPresent();
        assertThat(count.get()).isEqualTo(50L);
    }
}
