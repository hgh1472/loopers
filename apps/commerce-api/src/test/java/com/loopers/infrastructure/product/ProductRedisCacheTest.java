package com.loopers.infrastructure.product;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductRedisCacheTest {

    @InjectMocks
    private ProductRedisCache productRedisCache;
    @Mock
    private ProductCacheReader productCacheReader;
    @Mock
    private ProductCacheWriter productCacheWriter;

    @Nested
    @DisplayName("상품 카운트 캐시 쓰기 시,")
    class WriteProductCount {

        @Test
        @DisplayName("브랜드 ID가 null이 아니면, 브랜드 상품 카운트 캐시를 작성한다.")
        void writeBrandProductCount() {
            Long brandId = 1L;
            Long count = 10L;

            productRedisCache.writeProductCount(brandId, count);

            verify(productCacheWriter).writeBrandProductCount(brandId, count);
        }

        @Test
        @DisplayName("브랜드 ID가 null이면, 전체 상품 카운트 캐시를 작성한다.")
        void writeAllProductCount() {
            Long count = 10L;

            productRedisCache.writeProductCount(null, count);

            verify(productCacheWriter).writeAllProductCount(count);
        }

        @Test
        @DisplayName("레디스 쓰기 연산 실패 시, 예외가 발생하지 않고 로그를 남긴다.")
        void catchException_whenRedisFail() {
            Long brandId = 1L;
            Long count = 10L;

            doThrow(new RuntimeException("레디스 쓰기 실패")).when(productCacheWriter).writeBrandProductCount(brandId, count);

            assertDoesNotThrow(() -> productRedisCache.writeProductCount(brandId, count));
        }
    }

    @Nested
    @DisplayName("상품 카운트 캐시 읽기 시,")
    class ReadProductCount {

        @Test
        @DisplayName("브랜드 ID가 null이 아니면, 브랜드 상품 카운트 캐시를 읽는다.")
        void readBrandProductCount() {
            Long brandId = 1L;

            productRedisCache.readProductCount(brandId);

            verify(productCacheReader).getBrandProductCount(brandId);
        }

        @Test
        @DisplayName("브랜드 ID가 null이면, 전체 상품 카운트 캐시를 읽는다.")
        void readAllProductCount() {
            productRedisCache.readProductCount(null);

            verify(productCacheReader).getAllProductCount();
        }

        @Test
        @DisplayName("레디스 읽기 연산 실패 시, 빈 Optional을 반환한다.")
        void returnEmptyOptional_whenRedisFail() {
            Long brandId = 1L;

            doThrow(new RuntimeException("레디스 읽기 실패")).when(productCacheReader).getBrandProductCount(brandId);

            Optional<Long> result = productRedisCache.readProductCount(brandId);

            assertTrue(result.isEmpty());
        }
    }
}
