package com.loopers.domain.cache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
class CacheServiceIntegrationTest {

    @MockitoSpyBean
    private CacheService cacheService;
    @MockitoBean
    private ProductCacheRepository productCacheRepository;

    @Nested
    @DisplayName("상품 캐시 조회 시,")
    class FindCache {
        @Test
        @DisplayName("캐시 조회 중 실패한다면, circuit breaker의 폴백 메서드가 호출된다.")
        void callback_whenCacheFails() {
            given(productCacheRepository.findProductDetail(anyString()))
                    .willThrow(new RuntimeException("캐시 조회 실패"));

            cacheService.findProductDetail(1L);

            verify(cacheService, times(1))
                    .findProductDetailFallback(any(), any(RuntimeException.class));
        }
    }
}
