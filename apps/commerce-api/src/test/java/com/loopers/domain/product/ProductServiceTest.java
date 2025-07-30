package com.loopers.domain.product;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Nested
    @DisplayName("상품 조회 시,")
    class Find {
        @DisplayName("존재하지 않는 상품 ID라면, null을 반환한다.")
        @Test
        void returnNull_whenProductDoesNotExist() {
            Long productId = 2L;
            given(productRepository.findById(productId))
                    .willReturn(Optional.empty());

            ProductInfo productInfo = productService.findProduct(new ProductCommand.Find(productId));

            assertThat(productInfo).isNull();
        }
    }

    @Nested
    @DisplayName("ID 목록으로 상품 조회 시,")
    class GetProducts {
        @DisplayName("ID 목록이 비어있다면, BAD_REQUEST 예외를 발생시킨다.")
        @NullAndEmptySource
        @ParameterizedTest(name = "productIds = {0}")
        void throwBadRequestException_whenProductIdsIsEmpty(Set<Long> productIds) {
            CoreException thrown = assertThrows(CoreException.class, () -> productService.getProducts(productIds));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "상품 ID 목록이 비어 있습니다."));
        }

        @DisplayName("상품 조회 결과가 없을 시, NOT_FOUND 예외를 발생시킨다.")
        @Test
        void throwNotFoundException_whenProductIdsContainNonExistentId() {
            Set<Long> productIds = Set.of(1L, 2L);
            given(productRepository.findByIds(productIds))
                    .willReturn(List.of());

            CoreException thrown = assertThrows(CoreException.class, () -> productService.getProducts(productIds));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품이 포함되어 있습니다."));
        }

        @DisplayName("조회 항목 중 없는 상품이 있을 경우, NOT_FOUND 예외를 발생시킨다.")
        @Test
        void throwNotFoundException_whenSomeProductsDoNotExist() {
            Set<Long> productIds = Set.of(1L, 2L);
            Product product = Product.create(new ProductCommand.Create(1L, "Product 1", new BigDecimal("10000"), "ON_SALE"));
            given(productRepository.findByIds(productIds))
                    .willReturn(List.of(product));

            CoreException thrown = assertThrows(CoreException.class, () -> productService.getProducts(productIds));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품이 포함되어 있습니다."));
        }
    }
}
