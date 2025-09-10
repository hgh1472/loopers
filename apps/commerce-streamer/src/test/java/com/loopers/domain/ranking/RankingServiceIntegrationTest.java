package com.loopers.domain.ranking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.loopers.domain.ranking.RankingCommand.Sale;
import com.loopers.infrastructure.ranking.WeightJpaRepository;
import com.loopers.key.MetricsKeys;
import com.loopers.key.WeightKeys;
import com.loopers.utils.DatabaseCleanUp;
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
    private DailyRankingRepository dailyRankingRepository;
    @Autowired
    private WeightJpaRepository weightJpaRepository;
    @Autowired
    private RedisCleanUp redisCleanUp;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        redisCleanUp.truncateAll();
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("상품 별 집계 시,")
    class Record {
        @Test
        @DisplayName("좋아요가 증가하는 경우,좋아요의 score를 증가시킨다.")
        void recordLikeCounts() {
            LocalDate now = LocalDate.now();
            masterRedisTemplate.opsForHash().put(WeightKeys.WEIGHT.getKey(now), "likes", "0.2");
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
            LocalDate now = LocalDate.now();
            masterRedisTemplate.opsForHash().put(WeightKeys.WEIGHT.getKey(now), "views", "0.1");
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
            LocalDate now = LocalDate.now();
            masterRedisTemplate.opsForHash().put(WeightKeys.WEIGHT.getKey(now), "sales", "0.7");
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

    @Nested
    @DisplayName("일별 랭킹 업데이트 시,")
    class UpdateDailyRankings {
        @Test
        @DisplayName("오늘 점수의 10%가 내일 랭킹에 반영된다.")
        void carryOverScores() {
            LocalDate today = LocalDate.now();
            masterRedisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), "1", 100);
            masterRedisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), "2", 200);
            masterRedisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), "3", 300);
            masterRedisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), "4", 400);
            Weight weight = new Weight(0.1, 0.2, 0.7);
            weight.activate();
            weightJpaRepository.save(weight);

            rankingService.updateDailyRankings(new RankingCommand.UpdateDailyRanking(today));

            assertThat(masterRedisTemplate.opsForZSet().score(MetricsKeys.PRODUCT_SCORE.getKey(today.plusDays(1)), "1")).isEqualTo(10.0);
            assertThat(masterRedisTemplate.opsForZSet().score(MetricsKeys.PRODUCT_SCORE.getKey(today.plusDays(1)), "2")).isEqualTo(20.0);
            assertThat(masterRedisTemplate.opsForZSet().score(MetricsKeys.PRODUCT_SCORE.getKey(today.plusDays(1)), "3")).isEqualTo(30.0);
            assertThat(masterRedisTemplate.opsForZSet().score(MetricsKeys.PRODUCT_SCORE.getKey(today.plusDays(1)), "4")).isEqualTo(40.0);
        }

        @Test
        @DisplayName("오늘 랭킹의 상위 20개의 상품이 저장된다.")
        void saveDailyRankings() {
            LocalDate today = LocalDate.now();
            masterRedisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), "1", 100);
            masterRedisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), "2", 200);
            masterRedisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), "3", 300);
            masterRedisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), "4", 400);
            Weight weight = new Weight(0.1, 0.2, 0.7);
            weight.activate();
            weightJpaRepository.save(weight);

            rankingService.updateDailyRankings(new RankingCommand.UpdateDailyRanking(today));

            List<DailyRanking> dailyRankings = dailyRankingRepository.findDailyRankings(today);
            assertThat(dailyRankings).hasSize(4);
            assertThat(dailyRankings)
                    .extracting("productId", "rank")
                    .containsExactlyInAnyOrder(
                            tuple(4L, 1),
                            tuple(3L, 2),
                            tuple(2L, 3),
                            tuple(1L, 4)
                    );
        }

        @Test
        @DisplayName("이전 가중치는 비활성화되고, 최신 가중치가 활성화된다.")
        void activateLatestWeight() {
            LocalDate today = LocalDate.now();
            Weight previousWeight = new Weight(0.1, 0.2, 0.7);
            previousWeight.activate();
            weightJpaRepository.save(previousWeight);
            Weight latestWeight = new Weight(0.3, 0.3, 0.4);
            weightJpaRepository.save(latestWeight);

            rankingService.updateDailyRankings(new RankingCommand.UpdateDailyRanking(today));

            Weight activate = weightJpaRepository.findByActivateTrue();
            assertThat(activate)
                    .extracting("likeWeight", "viewWeight", "salesWeight")
                    .containsExactly(0.3, 0.3, 0.4);
        }

        @Test
        @DisplayName("새 가중치가 Redis에 반영된다.")
        void updateWeightsInRedis() {
            LocalDate today = LocalDate.now();
            Weight previousWeight = new Weight(0.1, 0.2, 0.7);
            previousWeight.activate();
            weightJpaRepository.save(previousWeight);
            Weight latestWeight = new Weight(0.3, 0.3, 0.4);
            weightJpaRepository.save(latestWeight);

            rankingService.updateDailyRankings(new RankingCommand.UpdateDailyRanking(today));

            assertThat(masterRedisTemplate.opsForHash().entries(WeightKeys.WEIGHT.getKey(today.plusDays(1))))
                    .containsEntry("likes", "0.3")
                    .containsEntry("views", "0.3")
                    .containsEntry("sales", "0.4");
        }
    }
}
