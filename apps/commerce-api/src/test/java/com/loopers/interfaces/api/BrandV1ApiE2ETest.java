package com.loopers.interfaces.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandCommand.Create;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.interfaces.api.brand.BrandV1Dto;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BrandV1ApiE2ETest {
    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;
    private final BrandRepository brandRepository;

    @Autowired
    public BrandV1ApiE2ETest(TestRestTemplate testRestTemplate, DatabaseCleanUp databaseCleanUp,
                             BrandRepository brandRepository) {
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
        this.brandRepository = brandRepository;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("GET /api/v1/brands/{brandId}")
    class Get {
        final String BASE_URL = "/api/v1/brands/";

        @DisplayName("존재하지 않는 브랜드 조회 시, NOT_FOUND 예외를 반환한다.")
        @Test
        void throwNotFoundException_whenBrandDoesNotExist() {
            Long nonExistBrandId = 999L;
            String requestUrl = BASE_URL + nonExistBrandId.toString();
            ParameterizedTypeReference<ApiResponse<BrandV1Dto.BrandResponse>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<BrandV1Dto.BrandResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(null), responseType);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }

        @DisplayName("존재하는 브랜드 조회 시, 브랜드 정보를 반환한다.")
        @Test
        void returnBrandResponse_whenBrandExists() {
            Brand save = brandRepository.save(Brand.create(new Create("브랜드", "브랜드 설명")));
            Long existingBrandId = save.getId();
            String requestUrl = BASE_URL + existingBrandId.toString();
            ParameterizedTypeReference<ApiResponse<BrandV1Dto.BrandResponse>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<BrandV1Dto.BrandResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(null), responseType);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().id()).isEqualTo(existingBrandId),
                    () -> assertThat(response.getBody().data().name()).isEqualTo(save.getName()),
                    () -> assertThat(response.getBody().data().description()).isEqualTo(save.getDescription())
            );
        }
    }
}
