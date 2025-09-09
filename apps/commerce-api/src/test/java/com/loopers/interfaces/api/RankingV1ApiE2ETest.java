package com.loopers.interfaces.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.loopers.domain.PageResponse;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandCommand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductRepository;
import com.loopers.interfaces.api.ranking.RankingV1Dto;
import com.loopers.key.MetricsKeys;
import com.loopers.utils.DatabaseCleanUp;
import com.loopers.utils.RedisCleanUp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RankingV1ApiE2ETest {
    @Autowired
    private RedisTemplate<String, String> defaultRedisTemplate;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private RedisCleanUp redisCleanUp;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
        redisCleanUp.truncateAll();
    }

    @Nested
    @DisplayName("GET /api/v1/rankings")
    class GetRankings {
        public static final String URL = "/api/v1/rankings";

        @Test
        @DisplayName("랭킹 상품들을 조회한다.")
        void getRankings() {
            Brand brand1 = brandRepository.save(Brand.create(new BrandCommand.Create("브랜드 1", "BRAND 1")));
            Brand brand2 = brandRepository.save(Brand.create(new BrandCommand.Create("브랜드 2", "BRAND 2")));
            List<Product> products = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                Brand brand = (i % 2 == 0) ? brand1 : brand2;
                products.add(productRepository.save(Product.create(new ProductCommand.Create(brand.getId(), "상품 " + i, new BigDecimal("1000"), "ON_SALE"))));
                defaultRedisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(LocalDate.now()), String.valueOf(products.get(i - 1).getId()), 100 * (11 - i));
            }
            LocalDate today = LocalDate.now();

            ParameterizedTypeReference<ApiResponse<PageResponse<RankingV1Dto.RankingResponse>>> response = new ParameterizedTypeReference<>() {
            };

            String uri = UriComponentsBuilder.fromPath(URL)
                    .queryParam("page", 2)
                    .queryParam("size", 5)
                    .toUriString();
            HttpHeaders httpHeaders = new HttpHeaders();

            ResponseEntity<ApiResponse<PageResponse<RankingV1Dto.RankingResponse>>> result =
                    restTemplate.exchange(uri, HttpMethod.GET, null, response);

            assertThat(result.getBody().data().getTotalElements()).isEqualTo(10L);
            assertThat(result.getBody().data().getTotalPages()).isEqualTo(2);
            assertThat(result.getBody().data().getPageNumber()).isEqualTo(2);
            assertThat(result.getBody().data().getPageSize()).isEqualTo(5);
            assertThat(result.getBody().data().getContent()).hasSize(5);
            assertThat(result.getBody().data().getContent())
                    .extracting("productId", "rank")
                    .containsExactlyInAnyOrder(
                            tuple(products.get(5).getId(), 6L),
                            tuple(products.get(6).getId(), 7L),
                            tuple(products.get(7).getId(), 8L),
                            tuple(products.get(8).getId(), 9L),
                            tuple(products.get(9).getId(), 10L)
                    );
        }
    }
}
