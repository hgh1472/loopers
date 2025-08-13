package com.loopers.interfaces.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.loopers.domain.PageResponse;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandCommand;
import com.loopers.domain.brand.BrandRepository;
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
import com.loopers.interfaces.api.product.ProductV1Dto;
import com.loopers.utils.DatabaseCleanUp;
import com.loopers.utils.RedisCleanUp;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductV1ApiE2ETest {

    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;
    private final RedisCleanUp redisCleanUp;
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final StockRepository stockRepository;
    private final ProductLikeRepository productLikeRepository;
    private final UserRepository userRepository;
    private final ProductCountRepository productCountRepository;

    @Autowired
    public ProductV1ApiE2ETest(TestRestTemplate testRestTemplate, DatabaseCleanUp databaseCleanUp, RedisCleanUp redisCleanUp,
                               ProductRepository productRepository, BrandRepository brandRepository,
                               StockRepository stockRepository, ProductLikeRepository productLikeRepository,
                               UserRepository userRepository, ProductCountRepository productCountRepository) {
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
        this.redisCleanUp = redisCleanUp;
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.stockRepository = stockRepository;
        this.productLikeRepository = productLikeRepository;
        this.userRepository = userRepository;
        this.productCountRepository = productCountRepository;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
        redisCleanUp.truncateAll();
    }

    @Nested
    @DisplayName("GET /api/v1/products/{productId}")
    class GetProduct {

        @DisplayName("존재하지 않은 상품 조회 시, NOT_FOUND 예외를 반환한다.")
        @Test
        void returnNotFoundException_whenProductDoesNotExist() {
            Long nonExistProductId = 999L;
            Long userId = 1L;
            String requestUrl = "/api/v1/products/" + nonExistProductId;

            ParameterizedTypeReference<ApiResponse<ProductV1Dto.ProductResponse>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<ProductV1Dto.ProductResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(null), responseType);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @DisplayName("미로그인 유저가 상품 조회 시, 상품 좋아요 여부는 false로 반환한다.")
        @Test
        void returnLikeFalse_whenUserDoesNotLogin() {
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

            String requestUrl = "/api/v1/products/" + product.getId();
            ParameterizedTypeReference<ApiResponse<ProductV1Dto.ProductResponse>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<ProductV1Dto.ProductResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(null), responseType);

            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody().data().id()).isEqualTo(product.getId()),
                    () -> assertThat(response.getBody().data().brandName()).isEqualTo(brand.getName()),
                    () -> assertThat(response.getBody().data().productName()).isEqualTo(product.getName()),
                    () -> assertThat(response.getBody().data().price()).isEqualTo(product.getPrice().getValue()),
                    () -> assertThat(response.getBody().data().quantity()).isEqualTo(stock.getQuantity().getValue()),
                    () -> assertThat(response.getBody().data().likeCount()).isEqualTo(2L),
                    () -> assertThat(response.getBody().data().isLiked()).isFalse()
            );
        }

        @DisplayName("로그인 유저가 상품 조회 시, 좋아요 한 상품일 경우, 상품 좋아요 여부는 true로 반환한다.")
        @Test
        void returnLikeTrue_whenProductLikeExists_withUser() {
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

            String requestUrl = "/api/v1/products/" + product.getId().toString();
            ParameterizedTypeReference<ApiResponse<ProductV1Dto.ProductResponse>> responseType = new ParameterizedTypeReference<>() {
            };

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-USER-ID", user.getId().toString());
            ResponseEntity<ApiResponse<ProductV1Dto.ProductResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(httpHeaders), responseType);

            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody().data().id()).isEqualTo(product.getId()),
                    () -> assertThat(response.getBody().data().brandName()).isEqualTo(brand.getName()),
                    () -> assertThat(response.getBody().data().productName()).isEqualTo(product.getName()),
                    () -> assertThat(response.getBody().data().price()).isEqualTo(product.getPrice().getValue()),
                    () -> assertThat(response.getBody().data().quantity()).isEqualTo(stock.getQuantity().getValue()),
                    () -> assertThat(response.getBody().data().likeCount()).isEqualTo(2L),
                    () -> assertThat(response.getBody().data().isLiked()).isTrue()
            );
        }
    }

    @Nested
    @DisplayName("GET /api/v1/products")
    class ProductCard {
        @DisplayName("상품 검색 시, 최신순 상품 목록을 반환한다.")
        @Test
        void returnProductList_whenSearchProducts() {
            User user = userRepository.save(User.create(new UserCommand.Join("login", "hgh1472@loopers.com", "1999-06-23", "MALE")));
            Brand brand = brandRepository.save(Brand.create(new BrandCommand.Create("브랜드", "브랜드 설명")));
            for (int i = 1; i <= 20; i++) {
                Product product = productRepository.save(Product.create(new ProductCommand.Create(brand.getId(), "제품", new BigDecimal(1000L), "ON_SALE")));
                productCountRepository.save(ProductCount.from(product.getId()));
            }

            String requestUrl = UriComponentsBuilder.fromPath("/api/v1/products")
                    .queryParam("brandId", brand.getId())
                    .queryParam("page", 2)
                    .queryParam("size", 10)
                    .queryParam("sort", "LATEST")
                    .toUriString();

            ParameterizedTypeReference<ApiResponse<PageResponse<ProductV1Dto.ProductCard>>> responseType = new ParameterizedTypeReference<>() {
            };
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-USER-ID", user.getId().toString());
            ResponseEntity<ApiResponse<PageResponse<ProductV1Dto.ProductCard>>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(httpHeaders), responseType);

            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody().data().getTotalPages()).isEqualTo(2),
                    () -> assertThat(response.getBody().data().getTotalElements()).isEqualTo(20L),
                    () -> assertThat(response.getBody().data().getContent().get(0).id()).isEqualTo(10L))
            ;
        }
    }
}
