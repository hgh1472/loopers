package com.loopers.domain.count;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.utils.DatabaseCleanUp;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductCountServiceIntegrationTest {

    @Autowired
    private ProductCountService productCountService;
    @Autowired
    private ProductCountRepository productCountRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("상품 카운트 동시성 테스트")
    class Concurrency {
        @DisplayName("좋아요 수 증가가 동시에 요청되는 경우, 정확히 계산되어야 한다.")
        @Test
        void incrementLike_concurrency() throws InterruptedException {
            productCountRepository.save(ProductCount.from(1L));
            ProductCountCommand.Increment command = new ProductCountCommand.Increment(1L);
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        productCountService.incrementLike(command);
                    } catch (Exception e) {
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();

            ProductCount productCount = productCountRepository.findBy(1L).orElseThrow();
            assertThat(productCount.getLikeCount()).isEqualTo(threadCount);
        }

        @DisplayName("좋아요 수 감소가 동시에 요청되는 경우, 정확히 계산되어야 한다.")
        @Test
        void decrementLike_concurrency() throws InterruptedException {
            ProductCount productCount = ProductCount.from(1L);
            int threadCount = 10;
            for (int i = 0; i < threadCount; i++) {
                productCount.incrementLike();
            }
            productCountRepository.save(productCount);
            ProductCountCommand.Decrement command = new ProductCountCommand.Decrement(1L);
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        productCountService.decrementLike(command);
                    } catch (Exception e) {
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();

            ProductCount find = productCountRepository.findBy(1L).orElseThrow();
            assertThat(find.getLikeCount()).isEqualTo(0);
        }
    }
}
