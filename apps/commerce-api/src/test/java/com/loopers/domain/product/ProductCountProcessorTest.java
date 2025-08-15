package com.loopers.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
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
class ProductCountProcessorTest {

    @InjectMocks
    private ProductCountProcessor productCountProcessor;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductCache productCache;

    @Nested
    class GetProductCount {

        @Test
        @DisplayName("전체 상품 카운트 조회 시, 캐시에 카운트가 있는 경우, 캐시에서 전체 상품 카운트를 조회한다.")
        void getProductCount_whenBrandIdIsNull() {
            Long expectedCount = 100L;
            given(productCache.readAllProductCount()).willReturn(Optional.of(100L));

            Long actualCount = productCountProcessor.getProductCount(null);

            assertThat(actualCount).isEqualTo(100L);
            verify(productCache, times(1)).readAllProductCount();
            verify(productRepository, times(0)).countAllProducts();
        }

        @Test
        @DisplayName("전체 상품 카운트 조회 시, 캐시에 카운트가 없는 경우, DB에서 전체 상품 카운트를 조회하고 캐시에 저장한다.")
        void getProductCount_whenBrandIdIsNullAndCacheIsEmpty() {
            Long expectedCount = 100L;
            given(productCache.readAllProductCount()).willReturn(Optional.empty());
            given(productRepository.countAllProducts()).willReturn(expectedCount);

            Long actualCount = productCountProcessor.getProductCount(null);

            assertThat(actualCount).isEqualTo(expectedCount);
            verify(productCache, times(1)).readAllProductCount();
            verify(productRepository, times(1)).countAllProducts();
            verify(productCache, times(1)).writeAllProductCount(expectedCount);
        }

        @Test
        @DisplayName("브랜드별 상품 카운트 조회 시, 캐시에 카운트가 있는 경우, 캐시에서 브랜드별 상품 카운트를 조회한다.")
        void getBrandProductCount_whenBrandIdIsNotNull() {
            Long brandId = 1L;
            Long expectedCount = 50L;
            given(productCache.readProductCount(brandId)).willReturn(Optional.of(expectedCount));

            Long actualCount = productCountProcessor.getProductCount(brandId);

            assertThat(actualCount).isEqualTo(expectedCount);
            verify(productCache, times(1)).readProductCount(brandId);
            verify(productRepository, times(0)).countBrandProducts(brandId);
        }

        @Test
        @DisplayName("브랜드별 상품 카운트 조회 시, 캐시에 카운트가 없는 경우, DB에서 브랜드별 상품 카운트를 조회하고 캐시에 저장한다.")
        void getBrandProductCount_whenBrandIdIsNotNullAndCacheIsEmpty() {
            Long brandId = 1L;
            Long expectedCount = 50L;
            given(productCache.readProductCount(brandId)).willReturn(Optional.empty());
            given(productRepository.countBrandProducts(brandId)).willReturn(expectedCount);

            Long actualCount = productCountProcessor.getProductCount(brandId);

            assertThat(actualCount).isEqualTo(expectedCount);
            verify(productCache, times(1)).readProductCount(brandId);
            verify(productRepository, times(1)).countBrandProducts(brandId);
            verify(productCache, times(1)).writeProductCount(brandId, expectedCount);
        }
    }
}
