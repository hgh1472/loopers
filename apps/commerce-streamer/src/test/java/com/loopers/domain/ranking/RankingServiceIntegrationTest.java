package com.loopers.domain.ranking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.loopers.domain.ranking.RankingCommand.Sale;
import com.loopers.key.MetricsKeys;
import com.loopers.key.WeightKeys;
import com.loopers.utils.RedisCleanUp;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RankingServiceIntegrationTest {
    @Autowired
    private RankingService rankingService;
    @Autowired
    private RedisTemplate<String, String> masterRedisTemplate;
    @Autowired
    private RedisCleanUp redisCleanUp;

    @AfterEach
    void tearDown() {
        redisCleanUp.truncateAll();
    }

    @Nested
    @DisplayName("상품 별 집계 시,")
    class Record {
        @Test
        @DisplayName("좋아요가 증가하는 경우,좋아요의 score를 증가시킨다.")
        void recordLikeCounts() {
            masterRedisTemplate.opsForHash().put(WeightKeys.WEIGHT.getKey(), WeightKeys.LIKE.getKey(), "0.2");
            LocalDate now = LocalDate.now();
            List<RankingCommand.Like> cmd = List.of(
                    new RankingCommand.Like(1L, 10L, now),
                    new RankingCommand.Like(2L, 20L, now),
                    new RankingCommand.Like(3L, 30L, now)
            );

            rankingService.recordLikeCounts(cmd);

            assertThat(masterRedisTemplate.opsForZSet().size(MetricsKeys.PRODUCT_SCORE.getKey(now))).isEqualTo(3);
            assertAll(
                    () -> assertThat(masterRedisTemplate.opsForZSet().rank(MetricsKeys.PRODUCT_SCORE.getKey(now), "1")).isEqualTo(0L),
                    () -> assertThat(masterRedisTemplate.opsForZSet().rank(MetricsKeys.PRODUCT_SCORE.getKey(now), "2")).isEqualTo(1L),
                    () -> assertThat(masterRedisTemplate.opsForZSet().rank(MetricsKeys.PRODUCT_SCORE.getKey(now), "3")).isEqualTo(2L),
                    () -> assertThat(masterRedisTemplate.opsForZSet().score(MetricsKeys.PRODUCT_SCORE.getKey(now), "1")).isEqualTo(2.0),
                    () -> assertThat(masterRedisTemplate.opsForZSet().score(MetricsKeys.PRODUCT_SCORE.getKey(now), "2")).isEqualTo(4.0),
                    () -> assertThat(masterRedisTemplate.opsForZSet().score(MetricsKeys.PRODUCT_SCORE.getKey(now), "3")).isEqualTo(6.0)
            );
        }

        @Test
        @DisplayName("조회수가 증가하는 경우, 증가된 조회수를 집계한다.")
        void recordViewCounts() {
            masterRedisTemplate.opsForHash().put(WeightKeys.WEIGHT.getKey(), WeightKeys.VIEW.getKey(), "0.1");
            LocalDate now = LocalDate.now();
            List<RankingCommand.View> cmd = List.of(
                    new RankingCommand.View(1L, 10L, now),
                    new RankingCommand.View(2L, 20L, now),
                    new RankingCommand.View(3L, 30L, now)
            );

            rankingService.recordViewCounts(cmd);

            assertThat(masterRedisTemplate.opsForZSet().size(MetricsKeys.PRODUCT_SCORE.getKey(now))).isEqualTo(3);
            assertAll(
                    () -> assertThat(masterRedisTemplate.opsForZSet().rank(MetricsKeys.PRODUCT_SCORE.getKey(now), "1")).isEqualTo(0L),
                    () -> assertThat(masterRedisTemplate.opsForZSet().rank(MetricsKeys.PRODUCT_SCORE.getKey(now), "2")).isEqualTo(1L),
                    () -> assertThat(masterRedisTemplate.opsForZSet().rank(MetricsKeys.PRODUCT_SCORE.getKey(now), "3")).isEqualTo(2L),
                    () -> assertThat(masterRedisTemplate.opsForZSet().score(MetricsKeys.PRODUCT_SCORE.getKey(now), "1")).isEqualTo(1.0),
                    () -> assertThat(masterRedisTemplate.opsForZSet().score(MetricsKeys.PRODUCT_SCORE.getKey(now), "2")).isEqualTo(2.0),
                    () -> assertThat(masterRedisTemplate.opsForZSet().score(MetricsKeys.PRODUCT_SCORE.getKey(now), "3")).isEqualTo(3.0)
            );
        }

        @Test
        @DisplayName("판매량이 증가하는 경우, 증가된 판매량을 집계한다.")
        void recordSalesCounts() {
            masterRedisTemplate.opsForHash().put(WeightKeys.WEIGHT.getKey(), WeightKeys.SALES.getKey(), "0.7");
            LocalDate now = LocalDate.now();
            List<Sale> cmd = List.of(
                    new Sale(1L, 10L, now),
                    new RankingCommand.Sale(2L, 20L, now),
                    new RankingCommand.Sale(3L, 30L, now)
            );

            rankingService.recordSalesCounts(cmd);

            assertThat(masterRedisTemplate.opsForZSet().size(MetricsKeys.PRODUCT_SCORE.getKey(now))).isEqualTo(3);
            assertAll(
                    () -> assertThat(masterRedisTemplate.opsForZSet().rank(MetricsKeys.PRODUCT_SCORE.getKey(now), "1")).isEqualTo(0L),
                    () -> assertThat(masterRedisTemplate.opsForZSet().rank(MetricsKeys.PRODUCT_SCORE.getKey(now), "2")).isEqualTo(1L),
                    () -> assertThat(masterRedisTemplate.opsForZSet().rank(MetricsKeys.PRODUCT_SCORE.getKey(now), "3")).isEqualTo(2L),
                    () -> assertThat(masterRedisTemplate.opsForZSet().score(MetricsKeys.PRODUCT_SCORE.getKey(now), "1")).isEqualTo(7.0),
                    () -> assertThat(masterRedisTemplate.opsForZSet().score(MetricsKeys.PRODUCT_SCORE.getKey(now), "2")).isEqualTo(14.0),
                    () -> assertThat(masterRedisTemplate.opsForZSet().score(MetricsKeys.PRODUCT_SCORE.getKey(now), "3")).isEqualTo(21.0)
            );
        }
    }
}
