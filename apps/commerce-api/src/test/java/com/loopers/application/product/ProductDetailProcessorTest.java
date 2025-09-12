package com.loopers.application.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.loopers.domain.brand.BrandCommand;
import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.cache.CacheCommand;
import com.loopers.domain.cache.CacheService;
import com.loopers.domain.cache.ProductDetailCache;
import com.loopers.domain.count.ProductCountCommand;
import com.loopers.domain.count.ProductCountInfo;
import com.loopers.domain.count.ProductCountService;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.ranking.RankingCommand;
import com.loopers.domain.ranking.RankingInfo;
import com.loopers.domain.ranking.RankingService;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockInfo;
import com.loopers.domain.stock.StockService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ProductDetailProcessorTest {

    @InjectMocks
    private ProductDetailProcessor productDetailProcessor;
    @Mock
    private CacheService cacheService;
    @Mock
    private ProductService productService;
    @Mock
    private BrandService brandService;
    @Mock
    private StockService stockService;
    @Mock
    private RankingService rankingService;
    @Mock
    private ProductCountService productCountService;


    @Nested
    @DisplayName("상품 상세 조회 시,")
    class GetProduct {

        @Test
        @DisplayName("상품이 존재하지 않으면, NOT_FOUND 예외를 발생시킨다.")
        void throwNotFoundException_whenProductNotFound() {
            given(productService.findProduct(new ProductCommand.Find(1L)))
                    .willReturn(null);

            CoreException thrown = assertThrows(CoreException.class, () -> productDetailProcessor.getProductDetail(1L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다."));
        }

        @Test
        @DisplayName("캐시가 존재하는 경우, 브랜드, 재고, 좋아요 수를 DB에 조회하지 않는다.")
        void getProductWithCache() {
            given(productService.findProduct(new ProductCommand.Find(1L)))
                    .willReturn(new ProductInfo(1L, 1L, "Product", new BigDecimal("100"), "ON_SALE"));
            ProductDetailCache cache = new ProductDetailCache(1L, "Brand", "Product", new BigDecimal("100"), "ON_SALE", 100L, 10L, 1L);
            given(cacheService.findProductDetail(1L))
                    .willReturn(Optional.of(cache));

            productDetailProcessor.getProductDetail(1L);

            verify(brandService, never()).findBy(any());
            verify(stockService, never()).findStock(any());
        }

        @Test
        @DisplayName("캐시가 존재하지 않는 경우, 브랜드, 재고, 좋아요 수를 DB에 조회한 후 상품 정보를 캐시에 저장한다.")
        void getProductWithoutCache() {
            ProductCommand.Find productCommand = new ProductCommand.Find(1L);
            given(productService.findProduct(productCommand))
                    .willReturn(new ProductInfo(1L, 1L, "Product", new BigDecimal("100"), "ON_SALE"));
            given(cacheService.findProductDetail(1L))
                    .willReturn(Optional.empty());
            BrandCommand.Find brandCommand = new BrandCommand.Find(1L);
            given(brandService.findBy(brandCommand))
                    .willReturn(new BrandInfo(1L, "Brand", "Brand Description"));
            StockCommand.Find stockCommand = new StockCommand.Find(1L);
            given(stockService.findStock(stockCommand))
                    .willReturn(new StockInfo(1L, 1L, 100L));
            ProductCountCommand.Get productCountCommand = new ProductCountCommand.Get(1L);
            given(productCountService.getProductCount(productCountCommand))
                    .willReturn(new ProductCountInfo(1L, 10L));
            given(rankingService.getProductRank(any()))
                    .willReturn(new RankingInfo(1L, 1L));

            productDetailProcessor.getProductDetail(1L);

            verify(productService, times(1)).findProduct(productCommand);
            verify(brandService, times(1)).findBy(brandCommand);
            verify(stockService, times(1)).findStock(stockCommand);
            verify(cacheService, times(1)).writeProductDetail(new CacheCommand.ProductDetail(
                    1L, "Brand", "Product", new BigDecimal("100"), "ON_SALE", 100L, 10L, 1L
            ));
            verify(rankingService, times(1)).getProductRank(any());
        }
    }
}
