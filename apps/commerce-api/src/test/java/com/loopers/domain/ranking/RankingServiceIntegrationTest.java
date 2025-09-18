package com.loopers.domain.ranking;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.domain.PageResponse;
import com.loopers.key.MetricsKeys;
import com.loopers.utils.DatabaseCleanUp;
import com.loopers.utils.RedisCleanUp;
import java.time.LocalDate;
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
    private RankingMvRepository rankingMvRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
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
    @DisplayName("랭킹 조회 시,")
    class Ranking {
        @Test
        @DisplayName("원하는 페이지의 랭킹 정보를 반환한다.")
        void getRanking() {
            LocalDate today = LocalDate.now();
            for (int i = 1; i <= 10; i++) {
                redisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), String.valueOf(i), 100 - (i - 1) * 10);
            }
            RankingCommand.Rankings command = new RankingCommand.Rankings(5, 2, today);

            PageResponse<RankingInfo> result = rankingService.getRankings(command);

            assertThat(result.getContent().size()).isEqualTo(5);
            assertThat(result.getContent().get(0).productId()).isEqualTo(6L);
            assertThat(result.getContent().get(0).rank()).isEqualTo(6L);
            assertThat(result.getContent().get(1).productId()).isEqualTo(7L);
            assertThat(result.getContent().get(1).rank()).isEqualTo(7L);
            assertThat(result.getTotalElements()).isEqualTo(10);
            assertThat(result.getTotalPages()).isEqualTo(2);
            assertThat(result.getPageNumber()).isEqualTo(2);
            assertThat(result.getPageSize()).isEqualTo(5);
        }

        @Test
        @DisplayName("사이즈보다 적게 존재하는 경우, 존재하는 만큼만 반환한다.")
        void getRankingWithLessSize() {
            LocalDate today = LocalDate.now();
            for (int i = 1; i <= 3; i++) {
                redisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), String.valueOf(i), 100 - (i - 1) * 10);
            }
            RankingCommand.Rankings command = new RankingCommand.Rankings(5, 1, today);

            PageResponse<RankingInfo> result = rankingService.getRankings(command);

            assertThat(result.getContent().size()).isEqualTo(3);
            assertThat(result.getContent().get(0).productId()).isEqualTo(1L);
            assertThat(result.getContent().get(0).rank()).isEqualTo(1L);
            assertThat(result.getContent().get(1).productId()).isEqualTo(2L);
            assertThat(result.getContent().get(1).rank()).isEqualTo(2L);
            assertThat(result.getContent().get(2).productId()).isEqualTo(3L);
            assertThat(result.getContent().get(2).rank()).isEqualTo(3L);
            assertThat(result.getTotalElements()).isEqualTo(3);
            assertThat(result.getTotalPages()).isEqualTo(1);
            assertThat(result.getPageNumber()).isEqualTo(1);
            assertThat(result.getPageSize()).isEqualTo(5);
        }

        @Test
        @DisplayName("1 미만의 페이지를 요청하는 경우, 1페이지를 반환한다.")
        void getRanking_withPageLessThanOne() {
            LocalDate today = LocalDate.now();
            for (int i = 1; i <= 10; i++) {
                redisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), String.valueOf(i), 100 - (i - 1) * 10);
            }
            RankingCommand.Rankings command = new RankingCommand.Rankings(5, 0, today);

            PageResponse<RankingInfo> result = rankingService.getRankings(command);

            assertThat(result.getContent().size()).isEqualTo(5);
            assertThat(result.getContent().get(0).productId()).isEqualTo(1L);
            assertThat(result.getContent().get(0).rank()).isEqualTo(1L);
            assertThat(result.getContent().get(1).productId()).isEqualTo(2L);
            assertThat(result.getContent().get(1).rank()).isEqualTo(2L);
            assertThat(result.getTotalElements()).isEqualTo(10);
            assertThat(result.getTotalPages()).isEqualTo(2);
            assertThat(result.getPageNumber()).isEqualTo(1);
            assertThat(result.getPageSize()).isEqualTo(5);
        }

        @Test
        @DisplayName("페이지 사이즈가 5 미만인 경우, 5로 고정하여 반환한다.")
        void getRanking_withSizeLessThanFive() {
            LocalDate today = LocalDate.now();
            for (int i = 1; i <= 10; i++) {
                redisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), String.valueOf(i), 100 - (i - 1) * 10);
            }
            RankingCommand.Rankings command = new RankingCommand.Rankings(3, 1, today);

            PageResponse<RankingInfo> result = rankingService.getRankings(command);

            assertThat(result.getContent().size()).isEqualTo(5);
            assertThat(result.getContent().get(0).productId()).isEqualTo(1L);
            assertThat(result.getContent().get(0).rank()).isEqualTo(1L);
            assertThat(result.getContent().get(1).productId()).isEqualTo(2L);
            assertThat(result.getContent().get(1).rank()).isEqualTo(2L);
            assertThat(result.getTotalElements()).isEqualTo(10);
            assertThat(result.getTotalPages()).isEqualTo(2);
            assertThat(result.getPageNumber()).isEqualTo(1);
            assertThat(result.getPageSize()).isEqualTo(5);
        }

        @Test
        @DisplayName("페이지 사이즈가 20 초과인 경우, 20으로 고정하여 반환한다.")
        void getRanking_withSizeMoreThanTwenty() {
            LocalDate today = LocalDate.now();
            for (int i = 1; i <= 30; i++) {
                redisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), String.valueOf(i), 100 - (i - 1) * 10);
            }
            RankingCommand.Rankings command = new RankingCommand.Rankings(25, 1, today);

            PageResponse<RankingInfo> result = rankingService.getRankings(command);

            assertThat(result.getContent().size()).isEqualTo(20);
            assertThat(result.getContent().get(0).productId()).isEqualTo(1L);
            assertThat(result.getContent().get(0).rank()).isEqualTo(1L);
            assertThat(result.getContent().get(1).productId()).isEqualTo(2L);
            assertThat(result.getContent().get(1).rank()).isEqualTo(2L);
            assertThat(result.getTotalElements()).isEqualTo(30);
            assertThat(result.getTotalPages()).isEqualTo(2);
            assertThat(result.getPageNumber()).isEqualTo(1);
            assertThat(result.getPageSize()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("주간 랭킹 조회 시,")
    class WeeklyRanking {
        @Test
        @DisplayName("해당 페이지의 주간 랭킹 정보를 반환한다.")
        void getWeeklyRanking() {
            for (int i = 1; i <= 20; i++) {
                WeeklyRankingProductMv mv = new WeeklyRankingProductMv((long) i, i, 100 - (i - 1) * 5.0, LocalDate.now());
                rankingMvRepository.save(mv);
            }

            PageResponse<RankingInfo> infos = rankingService.getWeeklyRankings(new RankingCommand.Rankings(10, 2, LocalDate.now()));

            assertThat(infos.getTotalElements()).isEqualTo(20);
            assertThat(infos.getTotalPages()).isEqualTo(2);
            assertThat(infos.getPageNumber()).isEqualTo(2);
            assertThat(infos.getPageSize()).isEqualTo(10);
            assertThat(infos.getContent().size()).isEqualTo(10);
            assertThat(infos.getContent().get(0).productId()).isEqualTo(11L);
            assertThat(infos.getContent().get(0).rank()).isEqualTo(11L);
            assertThat(infos.getContent().get(9).productId()).isEqualTo(20L);
            assertThat(infos.getContent().get(9).rank()).isEqualTo(20L);
        }

        @Test
        @DisplayName("사이즈보다 적게 존재하는 경우, 존재하는 만큼만 반환한다.")
        void getWeeklyRankingWithLessSize() {
            for (int i = 1; i <= 3; i++) {
                WeeklyRankingProductMv mv = new WeeklyRankingProductMv((long) i, i, 100 - (i - 1) * 5.0, LocalDate.now());
                rankingMvRepository.save(mv);
            }

            PageResponse<RankingInfo> infos = rankingService.getWeeklyRankings(new RankingCommand.Rankings(10, 1, LocalDate.now()));

            assertThat(infos.getTotalElements()).isEqualTo(3);
            assertThat(infos.getTotalPages()).isEqualTo(1);
            assertThat(infos.getPageNumber()).isEqualTo(1);
            assertThat(infos.getPageSize()).isEqualTo(10);
            assertThat(infos.getContent().size()).isEqualTo(3);
            assertThat(infos.getContent().get(0).productId()).isEqualTo(1L);
            assertThat(infos.getContent().get(0).rank()).isEqualTo(1L);
            assertThat(infos.getContent().get(2).productId()).isEqualTo(3L);
            assertThat(infos.getContent().get(2).rank()).isEqualTo(3L);
        }

        @Test
        @DisplayName("1 미만의 페이지를 요청하는 경우, 1페이지를 반환한다.")
        void getWeeklyRanking_withPageLessThanOne() {
            for (int i = 1; i <= 20; i++) {
                WeeklyRankingProductMv mv = new WeeklyRankingProductMv((long) i, i, 100 - (i - 1) * 5.0, LocalDate.now());
                rankingMvRepository.save(mv);
            }

            PageResponse<RankingInfo> infos = rankingService.getWeeklyRankings(new RankingCommand.Rankings(10, 0, LocalDate.now()));

            assertThat(infos.getTotalElements()).isEqualTo(20);
            assertThat(infos.getTotalPages()).isEqualTo(2);
            assertThat(infos.getPageNumber()).isEqualTo(1);
            assertThat(infos.getPageSize()).isEqualTo(10);
            assertThat(infos.getContent().size()).isEqualTo(10);
            assertThat(infos.getContent().get(0).productId()).isEqualTo(1L);
            assertThat(infos.getContent().get(0).rank()).isEqualTo(1L);
            assertThat(infos.getContent().get(9).productId()).isEqualTo(10L);
            assertThat(infos.getContent().get(9).rank()).isEqualTo(10L);
        }

        @Test
        @DisplayName("페이지 사이즈가 5 미만인 경우, 5로 고정하여 반환한다.")
        void getWeeklyRanking_withSizeLessThanFive() {
            for (int i = 1; i <= 20; i++) {
                WeeklyRankingProductMv mv = new WeeklyRankingProductMv((long) i, i, 100 - (i - 1) * 5.0, LocalDate.now());
                rankingMvRepository.save(mv);
            }

            PageResponse<RankingInfo> infos = rankingService.getWeeklyRankings(new RankingCommand.Rankings(3, 1, LocalDate.now()));

            assertThat(infos.getTotalElements()).isEqualTo(20);
            assertThat(infos.getTotalPages()).isEqualTo(4);
            assertThat(infos.getPageNumber()).isEqualTo(1);
            assertThat(infos.getPageSize()).isEqualTo(5);
            assertThat(infos.getContent().size()).isEqualTo(5);
            assertThat(infos.getContent().get(0).productId()).isEqualTo(1L);
            assertThat(infos.getContent().get(0).rank()).isEqualTo(1L);
            assertThat(infos.getContent().get(4).productId()).isEqualTo(5L);
            assertThat(infos.getContent().get(4).rank()).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("상품 랭킹 조회 시,")
    class ProductRank {
        @Test
        @DisplayName("해당 상품의 랭킹 정보가 존재하지 않으면, 상품의 rank를 null로 반환한다.")
        void getProductRank_withNoRanking() {
            LocalDate today = LocalDate.now();
            RankingCommand.Ranking command = new RankingCommand.Ranking(1L, today);

            RankingInfo result = rankingService.getProductRank(command);

            assertThat(result.rank()).isNull();
        }

        @Test
        @DisplayName("해당 상품의 순위를 반환한다.")
        void getProductRank() {
            LocalDate today = LocalDate.now();
            for (int i = 1; i <= 10; i++) {
                redisTemplate.opsForZSet().add(MetricsKeys.PRODUCT_SCORE.getKey(today), String.valueOf(i), 100 - (i - 1) * 10);
            }
            RankingCommand.Ranking command = new RankingCommand.Ranking(3L, today);

            RankingInfo result = rankingService.getProductRank(command);

            assertThat(result.productId()).isEqualTo(3L);
            assertThat(result.rank()).isEqualTo(3L);
        }
    }
}
