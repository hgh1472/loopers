package com.loopers.interfaces.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.loopers.domain.like.ProductLike;
import com.loopers.domain.like.ProductLikeCommand;
import com.loopers.domain.like.ProductLikeRepository;
import com.loopers.interfaces.api.like.LikeV1Dto;
import com.loopers.utils.DatabaseCleanUp;
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

    @Autowired
    public LikeV1ApiE2ETest(TestRestTemplate testRestTemplate, DatabaseCleanUp databaseCleanUp,
                            ProductLikeRepository productLikeRepository) {
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
        this.productLikeRepository = productLikeRepository;
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
            Long productId = 1L;
            Long userId = 1L;
            ParameterizedTypeReference<ApiResponse<LikeV1Dto.ProductLikeResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId.toString());
            ResponseEntity<ApiResponse<LikeV1Dto.ProductLikeResponse>> first =
                    testRestTemplate.exchange(BASE_URL + productId, HttpMethod.POST, new HttpEntity<>(headers), responseType);

            ResponseEntity<ApiResponse<LikeV1Dto.ProductLikeResponse>> second =
                    testRestTemplate.exchange(BASE_URL + productId, HttpMethod.POST, new HttpEntity<>(headers), responseType);

            assertAll(
                    () -> assertThat(first.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(second.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertEquals(first.getBody().data(), second.getBody().data())
            );
        }

        @DisplayName("상품 좋아요 생성 시, 상품 좋아요 정보를 반환한다.")
        @Test
        void returnProductLikeInfo_whenCreatingLike() {
            Long productId = 1L;
            Long userId = 1L;
            ParameterizedTypeReference<ApiResponse<LikeV1Dto.ProductLikeResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId.toString());

            ResponseEntity<ApiResponse<LikeV1Dto.ProductLikeResponse>> response =
                    testRestTemplate.exchange(BASE_URL + productId, HttpMethod.POST, new HttpEntity<>(headers), responseType);

            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody().data().productId()).isEqualTo(productId),
                    () -> assertThat(response.getBody().data().userId()).isEqualTo(userId)
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
            Long productId = 1L;
            Long userId = 1L;
            ParameterizedTypeReference<ApiResponse<LikeV1Dto.ProductLikeResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId.toString());
            ResponseEntity<ApiResponse<LikeV1Dto.ProductLikeResponse>> first =
                    testRestTemplate.exchange(BASE_URL + productId, HttpMethod.DELETE, new HttpEntity<>(headers), responseType);

            ResponseEntity<ApiResponse<LikeV1Dto.ProductLikeResponse>> second =
                    testRestTemplate.exchange(BASE_URL + productId, HttpMethod.DELETE, new HttpEntity<>(headers), responseType);

            assertAll(
                    () -> assertThat(first.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(second.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertEquals(first.getBody().data(), second.getBody().data())
            );
        }

        @DisplayName("상품 좋아요 취소 시, 취소한 좋아요 정보를 반환한다.")
        @Test
        void returnProductLikeInfo_whenCreatingLike() {
            Long productId = 1L;
            Long userId = 1L;
            productLikeRepository.save(ProductLike.create(new ProductLikeCommand.Create(productId, userId)));
            ParameterizedTypeReference<ApiResponse<LikeV1Dto.ProductLikeResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId.toString());

            ResponseEntity<ApiResponse<LikeV1Dto.ProductLikeResponse>> response =
                    testRestTemplate.exchange(BASE_URL + productId, HttpMethod.DELETE, new HttpEntity<>(headers), responseType);

            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody().data().productId()).isEqualTo(productId),
                    () -> assertThat(response.getBody().data().userId()).isEqualTo(userId)
            );
        }
    }
}
