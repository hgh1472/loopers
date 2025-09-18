package com.loopers.batch.job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.loopers.domain.ranking.DailyMetric;
import com.loopers.domain.ranking.MonthlyProductRankMv;
import com.loopers.domain.ranking.RankMvRepository;
import com.loopers.domain.ranking.WeeklyProductRankMv;
import com.loopers.infrastructure.ranking.DailyMetricJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import com.loopers.utils.RedisCleanUp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest(classes = {BatchTestConfig.class})
@SpringBatchTest
class RankingJobTest {

    @Autowired
    @Qualifier("rankingJobTest")
    private JobLauncherTestUtils rankingJob;
    @Autowired
    private DailyMetricJpaRepository dailyMetricJpaRepository;
    @Autowired
    private RankMvRepository rankMvRepository;
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
    @DisplayName("랭킹 배치 작업")
    class WeeklyRankingJob {
        @Test
        @DisplayName("전체 상품 중 주간 랭킹 300위까지만 저장된다.")
        void saveWeeklyTop300() throws Exception {
            LocalDate date = LocalDate.of(2025, 9, 17);
            for (long i = 1; i <= 350; i++) {
                dailyMetricJpaRepository.save(new DailyMetric(i, 10L, 20L, 10 * i, date));
            }
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("date", date.toString())
                    .addString("batch-id", UUID.randomUUID().toString())
                    .toJobParameters();

            rankingJob.launchJob(jobParameters);

            List<WeeklyProductRankMv> weeklyRankMv = rankMvRepository.findWeeklyRankMv(date.plusDays(1));
            assertThat(weeklyRankMv).hasSize(300);
        }

        @Test
        @DisplayName("각 상품의 주간 랭킹 정보가 저장된다.")
        void saveRankingInfo() throws Exception {
            LocalDate date = LocalDate.of(2025, 9, 17);
            for (long i = 1; i <= 10; i++) {
                dailyMetricJpaRepository.save(new DailyMetric(i, 10L, 20L, 10 * i, date));
            }
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("date", date.toString())
                    .addString("batch-id", UUID.randomUUID().toString())
                    .toJobParameters();

            rankingJob.launchJob(jobParameters);

            List<WeeklyProductRankMv> weeklyRankMv = rankMvRepository.findWeeklyRankMv(date.plusDays(1));
            assertThat(weeklyRankMv).extracting("productId", "rank")
                    .containsExactlyInAnyOrder(tuple(10L, 1), tuple(9L, 2), tuple(8L, 3),
                            tuple(7L, 4), tuple(6L, 5), tuple(5L, 6), tuple(4L, 7),
                            tuple(3L, 8), tuple(2L, 9), tuple(1L, 10));
        }

        @Test
        @DisplayName("전체 중 랭킹 300위까지만 저장된다.")
        void saveTop300() throws Exception {
            LocalDate date = LocalDate.of(2025, 9, 18);
            List<WeeklyProductRankMv> weeklyMvs = new ArrayList<>();
            for (long i = 1; i <= 350; i++) {
                WeeklyProductRankMv mv = new WeeklyProductRankMv(i, (int) (351 - i), 1 * (double) i, date);
                weeklyMvs.add(mv);
            }
            rankMvRepository.saveWeeklyRankingMvs(weeklyMvs);
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("date", date.toString())
                    .addString("batch-id", UUID.randomUUID().toString())
                    .toJobParameters();

            rankingJob.launchJob(jobParameters);

            List<MonthlyProductRankMv> monthlyRankMv = rankMvRepository.findMonthlyRankMv(date.plusDays(1));
            MonthlyProductRankMv first = rankMvRepository.findMonthlyRankMvByProductId(date.plusDays(1), 350L).orElseThrow();
            MonthlyProductRankMv last = rankMvRepository.findMonthlyRankMvByProductId(date.plusDays(1), 51L).orElseThrow();
            assertThat(monthlyRankMv).hasSize(300);
            assertThat(first.getRank()).isEqualTo(1);
            assertThat(last.getRank()).isEqualTo(300);
        }

        @Test
        @DisplayName("사용된 랭킹 버퍼는 삭제된다.")
        void deleteRankingBuffer() throws Exception {
            LocalDate date = LocalDate.of(2025, 9, 18);
            List<WeeklyProductRankMv> weeklyMvs = new ArrayList<>();
            for (long i = 1; i <= 10; i++) {
                WeeklyProductRankMv mv = new WeeklyProductRankMv(i, (int) (11 - i), 1 * (double) i, date);
                weeklyMvs.add(mv);
            }
            rankMvRepository.saveWeeklyRankingMvs(weeklyMvs);
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("date", date.toString())
                    .addString("batch-id", UUID.randomUUID().toString())
                    .toJobParameters();

            rankingJob.launchJob(jobParameters);

            Boolean hasKey = redisTemplate.hasKey("monthly_ranking");
            assertThat(hasKey).isFalse();
        }
    }
}
