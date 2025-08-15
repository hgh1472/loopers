package com.loopers.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.loopers.domain.PageResponse;
import com.loopers.domain.product.ProductCommand.Purchasable;
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
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductCountProcessor productCountProcessor;

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
    @DisplayName("ID 목록으로 구매 가능 상품 조회 시,")
    class GetPurchasableProducts {
        @DisplayName("ID 목록이 비어있다면, BAD_REQUEST 예외를 발생시킨다.")
        @NullAndEmptySource
        @ParameterizedTest(name = "productIds = {0}")
        void throwBadRequestException_whenProductIdsIsEmpty(Set<Long> productIds) {
            CoreException thrown = assertThrows(CoreException.class, () -> productService.getPurchasableProducts(new Purchasable(productIds)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "상품 ID 목록이 비어 있습니다."));
        }

        @DisplayName("ID 목록 중 구매 가능한 상품만 반환한다.")
        @Test
        void returnPurchasableProducts_whenProductIdsContainPurchasableProducts() {
            Set<Long> productIds = Set.of(1L, 2L);
            Product product1 = Product.create(new ProductCommand.Create(1L, "Product 1", new BigDecimal("100.00"), "ON_SALE"));
            Product product2 = Product.create(new ProductCommand.Create(2L, "Product 2", new BigDecimal("200.00"), "OUT_OF_STOCK"));
            given(productRepository.findByIds(productIds))
                    .willReturn(List.of(product1, product2));

            List<ProductInfo> result = productService.getPurchasableProducts(new Purchasable(productIds));

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("Product 1");
        }
    }

    @Nested
    @DisplayName("상품 목록 조회 시,")
    class Search {

        @DisplayName("페이지를 지정하지 않으면, 첫번째 페이지를 조회한다.")
        @Test
        void returnFirstPage_whenPageIsNotSpecified() {
            ProductCommand.Page command = new ProductCommand.Page(1L, null, 10, "LATEST");
            given(productRepository.search(new ProductParams.Search(1L, 0, 10, ProductParams.Sort.LATEST)))
                    .willReturn(Page.empty());
            given(productCountProcessor.getProductCount(1L))
                    .willReturn(10L);

            PageResponse<ProductInfo.Search> result = productService.search(command);

            verify(productRepository, times(1))
                    .search(new ProductParams.Search(1L, 0, 10, ProductParams.Sort.LATEST));
        }

        @DisplayName("페이지 크기가 30을 초과하면, 기본값인 10으로 조회한다.")
        @Test
        void returnPageSizeTen_whenSizeExceedsThirty() {
            ProductCommand.Page command = new ProductCommand.Page(1L, 0, 50, "LATEST");
            given(productRepository.search(new ProductParams.Search(1L, 0, 10, ProductParams.Sort.LATEST)))
                    .willReturn(Page.empty());
            given(productCountProcessor.getProductCount(1L))
                    .willReturn(10L);

            PageResponse<ProductInfo.Search> result = productService.search(command);

            verify(productRepository, times(1))
                    .search(new ProductParams.Search(1L, 0, 10, ProductParams.Sort.LATEST));
        }

        @DisplayName("페이지 사이즈를 지정하지 않으면, 기본값인 10으로 조회한다.")
        @Test
        void returnDefaultPageSize_whenSizeIsNotSpecified() {
            ProductCommand.Page command = new ProductCommand.Page(1L, 0, null, "LATEST");
            given(productRepository.search(new ProductParams.Search(1L, 0, 10, ProductParams.Sort.LATEST)))
                    .willReturn(Page.empty());
            given(productCountProcessor.getProductCount(1L))
                    .willReturn(10L);


            PageResponse<ProductInfo.Search> result = productService.search(command);

            verify(productRepository, times(1))
                    .search(new ProductParams.Search(1L, 0, 10, ProductParams.Sort.LATEST));
        }

        @DisplayName("정렬 기준이 유효하지 않으면, 기본값인 LATEST로 조회한다.")
        @Test
        void returnLatestSort_whenSortIsInvalid() {
            ProductCommand.Page command = new ProductCommand.Page(1L, 0, 10, "INVALID_SORT");
            given(productRepository.search(new ProductParams.Search(1L, 0, 10, ProductParams.Sort.LATEST)))
                    .willReturn(Page.empty());
            given(productCountProcessor.getProductCount(1L))
                    .willReturn(10L);


            PageResponse<ProductInfo.Search> result = productService.search(command);

            verify(productRepository, times(1))
                    .search(new ProductParams.Search(1L, 0, 10, ProductParams.Sort.LATEST));
        }
    }
}
