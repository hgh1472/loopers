package com.loopers.interfaces.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.loopers.domain.count.ProductCount;
import com.loopers.domain.count.ProductCountRepository;
import com.loopers.domain.like.ProductLike;
import com.loopers.domain.like.ProductLikeCommand;
import com.loopers.domain.like.ProductLikeRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserRepository;
import com.loopers.interfaces.api.like.LikeV1Dto;
import com.loopers.utils.DatabaseCleanUp;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LikeV1ApiE2ETest {
    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;
    private final ProductLikeRepository productLikeRepository;
    private final ProductCountRepository productCountRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public LikeV1ApiE2ETest(TestRestTemplate testRestTemplate, DatabaseCleanUp databaseCleanUp,
                            ProductLikeRepository productLikeRepository, ProductCountRepository productCountRepository,
                            UserRepository userRepository, ProductRepository productRepository) {
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
        this.productLikeRepository = productLikeRepository;
        this.productCountRepository = productCountRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("POST /api/v1/like/products/{productId}")
    class Like {
        final String BASE_URL = "/api/v1/like/products/";

        @DisplayName("상품 좋아요 생성 시, 기존 좋아요 여부 상관없이, 같은 응답을 반환한다.")
        @Test
        void returnSameResponse_RegardlessOfProductLikeExist() {
            User user = userRepository.save(User.create(new UserCommand.Join("LoginId", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Product product = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product", new BigDecimal("2000"), "ON_SALE")));
            productCountRepository.save(ProductCount.from(product.getId()));

            ParameterizedTypeReference<ApiResponse<LikeV1Dto.ProductLikeResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", user.getId().toString());
            ResponseEntity<ApiResponse<LikeV1Dto.ProductLikeResponse>> first =
                    testRestTemplate.exchange(BASE_URL + product.getId(), HttpMethod.POST, new HttpEntity<>(headers), responseType);

            ResponseEntity<ApiResponse<LikeV1Dto.ProductLikeResponse>> second =
                    testRestTemplate.exchange(BASE_URL + product.getId(), HttpMethod.POST, new HttpEntity<>(headers), responseType);

            assertAll(
                    () -> assertThat(first.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(second.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertEquals(first.getBody().data(), second.getBody().data())
            );
        }

        @DisplayName("상품 좋아요 생성 시, 상품 좋아요 정보를 반환한다.")
        @Test
        void returnProductLikeInfo_whenCreatingLike() {
            User user = userRepository.save(User.create(new UserCommand.Join("LoginId", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Product product = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product", new BigDecimal("2000"), "ON_SALE")));
            productCountRepository.save(ProductCount.from(product.getId()));
            ParameterizedTypeReference<ApiResponse<LikeV1Dto.ProductLikeResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", user.getId().toString());

            ResponseEntity<ApiResponse<LikeV1Dto.ProductLikeResponse>> response =
                    testRestTemplate.exchange(BASE_URL + product.getId(), HttpMethod.POST, new HttpEntity<>(headers), responseType);

            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody().data().productId()).isEqualTo(product.getId()),
                    () -> assertThat(response.getBody().data().userId()).isEqualTo(user.getId())
            );
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/like/products/{productId}")
    class Remove {
        final String BASE_URL = "/api/v1/like/products/";

        @DisplayName("상품 좋아요 취소 시, 기존 좋아요 여부 상관없이, 같은 응답을 반환한다.")
        @Test
        void returnSameResponse_regardlessOfProductLikeExist() {
            User user = userRepository.save(User.create(new UserCommand.Join("LoginId", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Product product = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product", new BigDecimal("2000"), "ON_SALE")));
            productCountRepository.save(ProductCount.from(product.getId()));
            ParameterizedTypeReference<ApiResponse<LikeV1Dto.ProductLikeResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", user.getId().toString());
            ResponseEntity<ApiResponse<LikeV1Dto.ProductLikeResponse>> first =
                    testRestTemplate.exchange(BASE_URL + product.getId(), HttpMethod.DELETE, new HttpEntity<>(headers), responseType);

            ResponseEntity<ApiResponse<LikeV1Dto.ProductLikeResponse>> second =
                    testRestTemplate.exchange(BASE_URL + product.getId(), HttpMethod.DELETE, new HttpEntity<>(headers), responseType);

            assertAll(
                    () -> assertThat(first.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(second.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertEquals(first.getBody().data(), second.getBody().data())
            );
        }

        @DisplayName("상품 좋아요 취소 시, 취소한 좋아요 정보를 반환한다.")
        @Test
        void returnProductLikeInfo_whenCreatingLike() {
            User user = userRepository.save(User.create(new UserCommand.Join("LoginId", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Product product = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product", new BigDecimal("2000"), "ON_SALE")));
            ProductCount productCount = ProductCount.from(product.getId());
            productCount.incrementLike();
            productCountRepository.save(productCount);

            productLikeRepository.save(ProductLike.create(new ProductLikeCommand.Create(product.getId(), user.getId())));
            ParameterizedTypeReference<ApiResponse<LikeV1Dto.ProductLikeResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", user.getId().toString());

            ResponseEntity<ApiResponse<LikeV1Dto.ProductLikeResponse>> response =
                    testRestTemplate.exchange(BASE_URL + product.getId(), HttpMethod.DELETE, new HttpEntity<>(headers), responseType);

            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody().data().productId()).isEqualTo(product.getId()),
                    () -> assertThat(response.getBody().data().userId()).isEqualTo(user.getId())
            );
        }
    }
}
