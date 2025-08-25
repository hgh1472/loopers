package com.loopers.domain.cache;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CacheServiceTest {

    @InjectMocks
    private CacheService cacheService;
    @Mock
    private ProductCacheRepository productCacheRepository;

    @Test
    @DisplayName("캐시 쓰기 중 예외가 발생하더라도, 예외를 그대로 던지지 않는다.")
    void doNotThrowException_whenCacheWriteFails() {
        CacheCommand.ProductDetail command = new CacheCommand.ProductDetail(1L, "Brand", "Product", new BigDecimal("100"), "ON_SALE", 100L, 10L);
        ProductDetailCache cache = ProductDetailCache.from(command);
        String key = String.format(CacheKeys.PRODUCT_DETAIL.key(), command.id());
        doThrow(RuntimeException.class)
                .when(productCacheRepository).writeProductDetail(cache, key, CacheKeys.PRODUCT_DETAIL.ttl());

        assertDoesNotThrow(() -> cacheService.writeProductDetail(command));
    }
}
