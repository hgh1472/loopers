package com.loopers.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.loopers.domain.PageResponse;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandCommand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.count.ProductCount;
import com.loopers.domain.count.ProductCountRepository;
import com.loopers.utils.DatabaseCleanUp;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProductIntegrationTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductCountRepository productCountRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("상품 목록 조회 시,")
    class Search {

        @DisplayName("최신 순 상품 목록을 조회할 수 있다.")
        @Test
        void searchLatestProducts() {
            Brand brand = brandRepository.save(Brand.create(new BrandCommand.Create("브랜드", "설명")));
            for (long i = 1; i <= 25; i++) {
                productRepository.save(Product.create(new ProductCommand.Create(brand.getId(), "Product " + i, BigDecimal.valueOf(100), "ON_SALE")));
                productCountRepository.save(ProductCount.from(i));
            }

            PageResponse<ProductInfo.Search> latest = productService.search(new ProductCommand.Page(null, 1, 5, "LATEST"));

            assertAll(
                    () -> assertThat(latest.getTotalElements()).isEqualTo(25),
                    () -> assertThat(latest.getTotalPages()).isEqualTo(5),
                    () -> assertThat(latest.getContent().size()).isEqualTo(5),
                    () -> assertThat(latest.getContent().get(0).id()).isEqualTo(20L),
                    () -> assertThat(latest.getContent().get(1).id()).isEqualTo(19L),
                    () -> assertThat(latest.getContent().get(2).id()).isEqualTo(18L),
                    () -> assertThat(latest.getContent().get(3).id()).isEqualTo(17L),
                    () -> assertThat(latest.getContent().get(4).id()).isEqualTo(16L)
            );
        }

        @DisplayName("가격 오름차순 상품 목록을 조회할 수 있다.")
        @Test
        void searchPriceAscProducts() {
            Brand brand = brandRepository.save(Brand.create(new BrandCommand.Create("브랜드", "설명")));
            for (long i = 1; i <= 25; i++) {
                productRepository.save(Product.create(new ProductCommand.Create(brand.getId(), "Product " + i, BigDecimal.valueOf(i * 10), "ON_SALE")));
                productCountRepository.save(ProductCount.from(i));
            }

            PageResponse<ProductInfo.Search> priceAsc = productService.search(new ProductCommand.Page(null, 2, 5, "PRICE_ASC"));

            assertAll(
                    () -> assertThat(priceAsc.getTotalElements()).isEqualTo(25),
                    () -> assertThat(priceAsc.getTotalPages()).isEqualTo(5),
                    () -> assertThat(priceAsc.getContent().size()).isEqualTo(5),
                    () -> assertThat(priceAsc.getContent().get(0).id()).isEqualTo(11L),
                    () -> assertThat(priceAsc.getContent().get(1).id()).isEqualTo(12L),
                    () -> assertThat(priceAsc.getContent().get(2).id()).isEqualTo(13L),
                    () -> assertThat(priceAsc.getContent().get(3).id()).isEqualTo(14L),
                    () -> assertThat(priceAsc.getContent().get(4).id()).isEqualTo(15L)
            );
        }

        @DisplayName("좋아요 내림차순 상품 목록을 조회할 수 있다.")
        @Test
        void searchLikeDescProducts() {
            Brand brand = brandRepository.save(Brand.create(new BrandCommand.Create("브랜드", "설명")));
            for (long i = 1; i <= 25; i++) {
                productRepository.save(Product.create(new ProductCommand.Create(brand.getId(), "Product " + i, BigDecimal.valueOf(100), "ON_SALE")));
                ProductCount productCount = ProductCount.from(i);
                for (long j = 1; j <= i; j++) {
                    productCount.incrementLike();
                }
                productCountRepository.save(productCount);
            }

            PageResponse<ProductInfo.Search> likeDesc = productService.search(new ProductCommand.Page(null, 1, 5, "LIKE_DESC"));

            assertAll(
                    () -> assertThat(likeDesc.getTotalElements()).isEqualTo(25),
                    () -> assertThat(likeDesc.getTotalPages()).isEqualTo(5),
                    () -> assertThat(likeDesc.getContent().size()).isEqualTo(5),
                    () -> assertThat(likeDesc.getContent().get(0).id()).isEqualTo(20L),
                    () -> assertThat(likeDesc.getContent().get(1).id()).isEqualTo(19L),
                    () -> assertThat(likeDesc.getContent().get(2).id()).isEqualTo(18L),
                    () -> assertThat(likeDesc.getContent().get(3).id()).isEqualTo(17L),
                    () -> assertThat(likeDesc.getContent().get(4).id()).isEqualTo(16L)
            );
        }

        @DisplayName("브랜드 ID로 필터링된 최신 순 상품 목록을 조회할 수 있다.")
        @Test
        void searchLatestProductsByBrandId() {
            Brand brand = brandRepository.save(Brand.create(new BrandCommand.Create("브랜드", "설명")));
            for (long i = 1; i <= 25; i++) {
                productRepository.save(Product.create(new ProductCommand.Create(i / 10, "Product " + i, BigDecimal.valueOf(100), "ON_SALE")));
                productCountRepository.save(ProductCount.from(i));
            }

            PageResponse<ProductInfo.Search> latest = productService.search(new ProductCommand.Page(brand.getId(), 0, 30, "LATEST"));
            assertAll(
                    () -> assertThat(latest.getTotalElements()).isEqualTo(10),
                    () -> assertThat(latest.getTotalPages()).isEqualTo(1),
                    () -> assertThat(latest.getContent().get(0).id()).isEqualTo(19L),
                    () -> assertThat(latest.getContent().get(1).id()).isEqualTo(18L),
                    () -> assertThat(latest.getContent().get(2).id()).isEqualTo(17L),
                    () -> assertThat(latest.getContent().get(3).id()).isEqualTo(16L),
                    () -> assertThat(latest.getContent().get(4).id()).isEqualTo(15L)
            );
        }

        @DisplayName("브랜드 ID로 필터링된 가격 오름차순 상품 목록을 조회할 수 있다.")
        @Test
        void searchPriceAscProductsByBrandId() {
            Brand brand = brandRepository.save(Brand.create(new BrandCommand.Create("브랜드", "설명")));
            for (long i = 1; i <= 25; i++) {
                productRepository.save(Product.create(new ProductCommand.Create(i / 10, "Product " + i, BigDecimal.valueOf(i * 10), "ON_SALE")));
                productCountRepository.save(ProductCount.from(i));
            }

            PageResponse<ProductInfo.Search> priceAsc = productService.search(new ProductCommand.Page(brand.getId(), 1, 6, "PRICE_ASC"));

            assertAll(
                    () -> assertThat(priceAsc.getTotalElements()).isEqualTo(10),
                    () -> assertThat(priceAsc.getTotalPages()).isEqualTo(2),
                    () -> assertThat(priceAsc.getContent().size()).isEqualTo(4),
                    () -> assertThat(priceAsc.getContent().get(0).id()).isEqualTo(16L),
                    () -> assertThat(priceAsc.getContent().get(1).id()).isEqualTo(17L),
                    () -> assertThat(priceAsc.getContent().get(2).id()).isEqualTo(18L),
                    () -> assertThat(priceAsc.getContent().get(3).id()).isEqualTo(19L)
            );
        }

        @DisplayName("브랜드 ID로 필터링된 좋아요 내림차순 상품 목록을 조회할 수 있다.")
        @Test
        void searchLikeDescProductsByBrandId() {
            Brand brand = brandRepository.save(Brand.create(new BrandCommand.Create("브랜드", "설명")));
            for (int i = 1; i <= 10; i++) {
                Product product = productRepository.save(Product.create(new ProductCommand.Create(brand.getId(), "Product " + i, BigDecimal.valueOf(100), "ON_SALE")));
                ProductCount productCount = ProductCount.from(product.getId());
                for (long j = 1; j <= i; j++) {
                    productCount.incrementLike();
                }
                productCountRepository.save(productCount);
            }
            for (int i = 11; i <= 15; i++) {
                Product product = productRepository.save(Product.create(new ProductCommand.Create((long) i, "Product " + i, BigDecimal.valueOf(100), "ON_SALE")));
                productCountRepository.save(ProductCount.from(product.getId()));
            }

            PageResponse<ProductInfo.Search> likeDesc = productService.search(new ProductCommand.Page(1L, 2, 3, "LIKE_DESC"));

            assertAll(
                    () -> assertThat(likeDesc.getTotalElements()).isEqualTo(10),
                    () -> assertThat(likeDesc.getTotalPages()).isEqualTo(4),
                    () -> assertThat(likeDesc.getContent().size()).isEqualTo(3),
                    () -> assertThat(likeDesc.getContent().get(0).id()).isEqualTo(4L),
                    () -> assertThat(likeDesc.getContent().get(1).id()).isEqualTo(3L),
                    () -> assertThat(likeDesc.getContent().get(2).id()).isEqualTo(2L)
            );
        }

        @DisplayName("브랜드 ID를 지정하지 않으면, 전체 상품 목록을 조회한다.")
        @Test
        void searchAllProductsWhenBrandIdIsNotSpecified() {
            Brand brand = brandRepository.save(Brand.create(new BrandCommand.Create("브랜드", "설명")));
            for (long i = 1; i <= 25; i++) {
                productRepository.save(Product.create(new ProductCommand.Create(brand.getId(), "Product " + i, BigDecimal.valueOf(100), "ON_SALE")));
                productCountRepository.save(ProductCount.from(i));
            }

            PageResponse<ProductInfo.Search> latest = productService.search(new ProductCommand.Page(null, 0, 30, "LATEST"));

            assertAll(
                    () -> assertThat(latest.getTotalElements()).isEqualTo(25),
                    () -> assertThat(latest.getTotalPages()).isEqualTo(1),
                    () -> assertThat(latest.getContent().size()).isEqualTo(25)
            );
        }
    }
}
