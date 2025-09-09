package com.loopers.application.ranking;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.domain.PageResponse;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandCommand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductRepository;
import com.loopers.key.MetricsKeys;
import com.loopers.utils.DatabaseCleanUp;
import com.loopers.utils.RedisCleanUp;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RankingFacadeIntegrationTest {
    @Autowired
    private RankingFacade rankingFacade;
    @Autowired
    private RedisTemplate<String, String> defaultRedisTemplate;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private RedisCleanUp redisCleanUp;

    @BeforeEach
    void setUp() {
        redisCleanUp.truncateAll();
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
        redisCleanUp.truncateAll();
    }

    @Nested
    @DisplayName("랭킹 조회 시,")
    class Ranking {
        @Test
        @DisplayName("상품 정보를 포함한 랭킹 정보를 반환한다.")
        void getRankProducts_withProductInfo() {
            Brand brand = brandRepository.save(Brand.create(new BrandCommand.Create("브랜드 1", "BRAND 1")));
            Product product1 = productRepository.save(Product.create(new ProductCommand.Create(brand.getId(), "상품 1", new BigDecimal("1000"), "ON_SALE")));
            Product product2 = productRepository.save(Product.create(new ProductCommand.Create(brand.getId(), "상품 2", new BigDecimal("1000"), "ON_SALE")));
            Product product3 = productRepository.save(Product.create(new ProductCommand.Create(brand.getId(), "상품 3", new BigDecimal("1000"), "ON_SALE")));
            Product product4 = productRepository.save(Product.create(new ProductCommand.Create(brand.getId(), "상품 4", new BigDecimal("1000"), "ON_SALE")));
            Product product5 = productRepository.save(Product.create(new ProductCommand.Create(brand.getId(), "상품 5", new BigDecimal("1000"), "ON_SALE")));
            LocalDate today = LocalDate.now();
            defaultRedisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), product1.getId().toString(), 500);
            defaultRedisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), product2.getId().toString(), 400);
            defaultRedisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), product3.getId().toString(), 300);
            defaultRedisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), product4.getId().toString(), 200);
            defaultRedisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), product5.getId().toString(), 100);
            RankingCriteria.Search cri = new RankingCriteria.Search(1, 5, today);

            PageResponse<RankingResult> result = rankingFacade.getRankProducts(cri);

            assertThat(result.getPageNumber()).isEqualTo(1);
            assertThat(result.getPageSize()).isEqualTo(5);
            assertThat(result.getTotalElements()).isEqualTo(5);
            assertThat(result.getTotalPages()).isEqualTo(1);
            assertThat(result.getContent()).hasSize(5);
            assertThat(result.getContent().get(0).productId()).isEqualTo(product1.getId());
            assertThat(result.getContent().get(0).rank()).isEqualTo(1L);
            assertThat(result.getContent().get(1).productId()).isEqualTo(product2.getId());
            assertThat(result.getContent().get(1).rank()).isEqualTo(2L);
            assertThat(result.getContent().get(2).productId()).isEqualTo(product3.getId());
            assertThat(result.getContent().get(2).rank()).isEqualTo(3L);
            assertThat(result.getContent().get(3).productId()).isEqualTo(product4.getId());
            assertThat(result.getContent().get(3).rank()).isEqualTo(4L);
            assertThat(result.getContent().get(4).productId()).isEqualTo(product5.getId());
            assertThat(result.getContent().get(4).rank()).isEqualTo(5L);
        }
    }
}
