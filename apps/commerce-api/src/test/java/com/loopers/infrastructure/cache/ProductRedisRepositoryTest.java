package com.loopers.infrastructure.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.loopers.domain.cache.ProductDetailCache;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductRedisRepositoryTest {

    @InjectMocks
    private ProductRedisRepository productRedisRepository;
    @Mock
    private ProductDetailCacheReader productDetailCacheReader;
    @Mock
    private ProductDetailCacheWriter productDetailCacheWriter;

    @Test
    @DisplayName("상품 상세 캐시를 가져오는 과정에서 JSON 직렬화 예외가 발생하면, 기존 캐시 무효화한다.")
    void evictCache_whenJsonProcessingException() throws JsonProcessingException {
        String key = "product:123";
        given(productDetailCacheReader.readProductCard(key)).willThrow(JsonProcessingException.class);

        Optional<ProductDetailCache> productDetail = productRedisRepository.findProductDetail(key);

        assertThat(productDetail).isEmpty();
        verify(productDetailCacheWriter, times(1)).evictProductCard(key);
    }

    @Test
    @DisplayName("상품 상세 캐시를 저장하는 과정에서 JSON 직렬화 예외가 발생하면, 예외를 catch하고 던지지 않는다.")
    void catchJsonProcessingException_whenSerializationFails() throws JsonProcessingException {
        ProductDetailCache productDetailCache = new ProductDetailCache(1L, "Brand", "Product", new BigDecimal("1000"), "ON_SALE", 100L, 10L);
        String key = "product:123";
        Duration ttl = Duration.ofSeconds(5L);
        doThrow(JsonProcessingException.class)
                .when(productDetailCacheWriter).writeProductCard(productDetailCache, key, ttl);

        assertDoesNotThrow(() -> productRedisRepository.writeProductDetail(productDetailCache, key, ttl));
    }


}
