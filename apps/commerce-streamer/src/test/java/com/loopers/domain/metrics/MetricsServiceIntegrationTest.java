package com.loopers.domain.metrics;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.domain.metrics.MetricCommand.IncrementViews;
import com.loopers.utils.DatabaseCleanUp;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MetricsServiceIntegrationTest {
    @Autowired
    private MetricsService metricsService;
    @Autowired
    private ProductMetricsRepository productMetricsRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Test
    @DisplayName("집계 데이터 동시성 테스트")
    void test() throws InterruptedException {
        LocalDate now = LocalDate.now();
        ProductMetrics productMetrics = new ProductMetrics(1L, now);
        productMetricsRepository.save(productMetrics);

        int threadCount = 9;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int count = i;
            executor.submit(() -> {
                try {
                    if (count % 3 == 0) {
                        metricsService.incrementLikeCount(new MetricCommand.IncrementLikes(1L, 2L, now));
                    } else if (count % 3 == 1) {
                        metricsService.incrementViewCount(new IncrementViews(1L, 5L, now));
                    } else {
                        metricsService.incrementSalesCount(new MetricCommand.IncrementSales(1L, 3L, now));
                    }
                } catch (Exception e) {
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        ProductMetrics result = productMetricsRepository.findByDailyMetrics(1L, now).orElseThrow();
        assertThat(result.getLikeCount()).isEqualTo(6);
        assertThat(result.getViewCount()).isEqualTo(15);
        assertThat(result.getSalesCount()).isEqualTo(9);
    }
}
