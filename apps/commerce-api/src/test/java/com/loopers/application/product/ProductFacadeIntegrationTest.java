package com.loopers.application.product;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.domain.PageResponse;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandCommand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.cache.ProductCacheRepository;
import com.loopers.domain.count.ProductCount;
import com.loopers.domain.count.ProductCountRepository;
import com.loopers.domain.like.ProductLike;
import com.loopers.domain.like.ProductLikeCommand;
import com.loopers.domain.like.ProductLikeRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.stock.Stock;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockRepository;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserRepository;
import com.loopers.key.MetricsKeys;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import com.loopers.utils.RedisCleanUp;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class ProductFacadeIntegrationTest {

    @Autowired
    private ProductFacade productFacade;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private ProductLikeRepository productLikeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ProductCountRepository productCountRepository;
    @Autowired
    private ProductCacheRepository productCacheRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private RedisCleanUp redisCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
        redisCleanUp.truncateAll();
    }

    @Nested
    @DisplayName("상품 조회 시,")
    class Get {
        @DisplayName("존재하지 않는 상품을 조회하면, NOT_FOUND 예외를 발생시킨다.")
        @Test
        void throwNotFoundException_whenProductDoesNotExist() {
            Long productId = 999L;
            Long userId = 1L;

            CoreException thrown = assertThrows(CoreException.class, () ->
                    productFacade.getProduct(new ProductCriteria.Get(productId, userId))
            );

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다."));
        }

        @DisplayName("유저 ID와 함께 존재하는 상품을 조회하면, 상품 좋아요 여부를 포함한 상세 정보를 반환한다.")
        @Test
        void returnProductInfo_whenProductExistsWithUser() {
            User user = userRepository.save(User.create(new UserCommand.Join("login", "hgh1472@loopers.com", "1999-06-23", "MALE")));
            Product init = Product.create(new ProductCommand.Create(1L, "제품", new BigDecimal(1000L), "ON_SALE"));
            Product product = productRepository.findById(productRepository.save(init).getId()).get();
            Brand brand = brandRepository.save(Brand.create(new BrandCommand.Create("브랜드", "브랜드 설명")));
            Stock stock = stockRepository.save(Stock.create(new StockCommand.Create(product.getId(), 100L)));
            ProductCount productCount = ProductCount.from(product.getId());
            productLikeRepository.save(ProductLike.create(new ProductLikeCommand.Create(product.getId(), user.getId())));
            productCount.incrementLike();
            productLikeRepository.save(ProductLike.create(new ProductLikeCommand.Create(product.getId(), user.getId() + 1)));
            productCount.incrementLike();
            productCountRepository.save(productCount);

            ProductResult productResult = productFacade.getProduct(new ProductCriteria.Get(product.getId(), user.getId()));

            assertAll(
                    () -> assertThat(productResult.id()).isEqualTo(product.getId()),
                    () -> assertThat(productResult.brandName()).isEqualTo(brand.getName()),
                    () -> assertThat(productResult.productName()).isEqualTo(product.getName()),
                    () -> assertThat(productResult.price()).isEqualTo(product.getPrice().getValue()),
                    () -> assertThat(productResult.status()).isEqualTo(product.getStatus().name()),
                    () -> assertThat(productResult.quantity()).isEqualTo(stock.getQuantity().getValue()),
                    () -> assertThat(productResult.likeCount()).isEqualTo(2L),
                    () -> assertThat(productResult.isLiked()).isTrue()
            );
        }

        @DisplayName("유저 ID 없이 존재하는 상품을 조회하면, 좋아요 여부를 제외한 상세 정보를 반환한다.")
        @Test
        void returnProductInfo_whenProductExistsWithoutUser() {
            User user = userRepository.save(User.create(new UserCommand.Join("login", "hgh1472@loopers.com", "1999-06-23", "MALE")));
            Product init = Product.create(new ProductCommand.Create(1L, "제품", new BigDecimal(1000L), "ON_SALE"));
            Product product = productRepository.findById(productRepository.save(init).getId()).get();
            Brand brand = brandRepository.save(Brand.create(new BrandCommand.Create("브랜드", "브랜드 설명")));
            Stock stock = stockRepository.save(Stock.create(new StockCommand.Create(product.getId(), 100L)));
            ProductCount productCount = ProductCount.from(product.getId());
            productLikeRepository.save(ProductLike.create(new ProductLikeCommand.Create(product.getId(), user.getId())));
            productCount.incrementLike();
            productLikeRepository.save(ProductLike.create(new ProductLikeCommand.Create(product.getId(), user.getId() + 1)));
            productCount.incrementLike();
            productCountRepository.save(productCount);

            ProductResult productResult = productFacade.getProduct(new ProductCriteria.Get(product.getId(), null));

            assertAll(
                    () -> assertThat(productResult.id()).isEqualTo(product.getId()),
                    () -> assertThat(productResult.brandName()).isEqualTo(brand.getName()),
                    () -> assertThat(productResult.productName()).isEqualTo(product.getName()),
                    () -> assertThat(productResult.price()).isEqualTo(product.getPrice().getValue()),
                    () -> assertThat(productResult.status()).isEqualTo(product.getStatus().name()),
                    () -> assertThat(productResult.quantity()).isEqualTo(stock.getQuantity().getValue()),
                    () -> assertThat(productResult.likeCount()).isEqualTo(2L),
                    () -> assertThat(productResult.isLiked()).isFalse()
            );
        }

        @Test
        @DisplayName("상품의 rank 정보가 존재한다면, rank 정보도 함께 반환한다.")
        void returnProductInfoWithRank_whenProductHasRank() {
            Product init = Product.create(new ProductCommand.Create(1L, "제품", new BigDecimal(1000L), "ON_SALE"));
            Product product = productRepository.findById(productRepository.save(init).getId()).get();
            Brand brand = brandRepository.save(Brand.create(new BrandCommand.Create("브랜드", "브랜드 설명")));
            Stock stock = stockRepository.save(Stock.create(new StockCommand.Create(product.getId(), 100L)));
            ProductCount productCount = ProductCount.from(product.getId());
            productCount.incrementLike();
            productCount.incrementLike();
            productCountRepository.save(productCount);
            LocalDate today = LocalDate.now();
            redisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), product.getId().toString(), 100);
            redisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), "100", 90);
            redisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), "101", 110);

            ProductResult productResult = productFacade.getProduct(new ProductCriteria.Get(product.getId(), null));

            assertThat(productResult.rank()).isEqualTo(2L);
        }

        @Test
        @DisplayName("상품의 rank 정보가 존재하지 않는다면, rank 정보는 null로 반환한다.")
        void returnProductInfoWithoutRank_whenProductDoesNotHaveRank() {
            Product init = Product.create(new ProductCommand.Create(1L, "제품", new BigDecimal(1000L), "ON_SALE"));
            Product product = productRepository.findById(productRepository.save(init).getId()).get();
            Brand brand = brandRepository.save(Brand.create(new BrandCommand.Create("브랜드", "브랜드 설명")));
            Stock stock = stockRepository.save(Stock.create(new StockCommand.Create(product.getId(), 100L)));
            ProductCount productCount = ProductCount.from(product.getId());
            productCount.incrementLike();
            productCount.incrementLike();
            productCountRepository.save(productCount);

            ProductResult productResult = productFacade.getProduct(new ProductCriteria.Get(product.getId(), null));

            assertThat(productResult.rank()).isNull();
        }
    }

    @Nested
    @DisplayName("상품 목록 조회 시,")
    class ProductCard {
        @DisplayName("로그인한 사용자의 경우, 상품 목록과 좋아요 여부를 포함한 페이지 결과를 반환한다.")
        @Test
        void returnProductPageResult_whenUserIsLoggedIn() {
            User user = userRepository.save(User.create(new UserCommand.Join("login", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Brand brand = brandRepository.save(Brand.create(new BrandCommand.Create("브랜드", "브랜드 설명")));
            for (int i = 1; i <= 20; i++) {
                Product product = productRepository.save(Product.create(new ProductCommand.Create(brand.getId(), "제품" + i, new BigDecimal(1000L * i), "ON_SALE")));
                productCountRepository.save(ProductCount.from(product.getId()));
                if (i <= 10) {
                    productLikeRepository.save(ProductLike.create(new ProductLikeCommand.Create(product.getId(), user.getId())));
                }
            }

            PageResponse<ProductResult.Card> latest = productFacade.searchProducts(new ProductCriteria.Search(brand.getId(), user.getId(), 2, 7, "LATEST"));

            assertAll(
                    () -> assertThat(latest.getTotalElements()).isEqualTo(20L),
                    () -> assertThat(latest.getTotalPages()).isEqualTo(3),
                    () -> assertThat(latest.getContent().size()).isEqualTo(7),
                    () -> assertThat(latest.getContent().get(0).id()).isEqualTo(13L),
                    () -> assertThat(latest.getContent().get(0).isLiked()).isFalse(),
                    () -> assertThat(latest.getContent().get(3).id()).isEqualTo(10L),
                    () -> assertThat(latest.getContent().get(6).isLiked()).isTrue(),
                    () -> assertThat(latest.getContent().get(6).id()).isEqualTo(7L),
                    () -> assertThat(latest.getContent().get(6).isLiked()).isTrue()
            );
        }

        @DisplayName("로그인하지 않은 사용자의 경우, 좋아여 여부는 전부 false로 페이지 결과를 반환한다.")
        @Test
        void returnProductPageResultWithoutLikes_whenUserIsNotLoggedIn() {
            User user = userRepository.save(User.create(new UserCommand.Join("login", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Brand brand = brandRepository.save(Brand.create(new BrandCommand.Create("브랜드", "브랜드 설명")));
            for (int i = 1; i <= 20; i++) {
                Product product = productRepository.save(Product.create(new ProductCommand.Create(brand.getId(), "제품" + i, new BigDecimal(1000L * i), "ON_SALE")));
                productCountRepository.save(ProductCount.from(product.getId()));
                if (i <= 10) {
                    productLikeRepository.save(ProductLike.create(new ProductLikeCommand.Create(product.getId(), user.getId())));
                }
            }

            PageResponse<ProductResult.Card> latest = productFacade.searchProducts(new ProductCriteria.Search(brand.getId(), null, 2, 7, "LATEST"));

            assertAll(
                    () -> assertThat(latest.getTotalElements()).isEqualTo(20L),
                    () -> assertThat(latest.getTotalPages()).isEqualTo(3),
                    () -> assertThat(latest.getContent().size()).isEqualTo(7),
                    () -> assertThat(latest.getContent().get(0).id()).isEqualTo(13L),
                    () -> assertThat(latest.getContent().get(0).isLiked()).isFalse(),
                    () -> assertThat(latest.getContent().get(3).id()).isEqualTo(10L),
                    () -> assertThat(latest.getContent().get(6).id()).isEqualTo(7L),
                    () -> assertThat(latest.getContent().get(6).isLiked()).isFalse()
            );
        }
    }
}
