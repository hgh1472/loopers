package com.loopers.domain.stock;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.utils.DatabaseCleanUp;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StockServiceIntegrationTest {

    @Autowired
    private StockService stockService;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void setUp() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("재고 차감 시,")
    class Deduct {

        @DisplayName("동시에 재고 차감을 요청해도, 재고는 정확히 차감된다.")
        @Test
        void deduct_concurrent() throws InterruptedException {
            stockRepository.save(Stock.create(new StockCommand.Create(1L, 100L)));
            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        stockService.deduct(new StockCommand.Deduct(1L, 10L));
                    } catch (Exception e) {
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();

            Stock stock = stockRepository.findByProductId(1L).orElseThrow();
            assertThat(stock.getQuantity().getValue()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("여러 재고 차감 시,")
    class DeductAll {
        @DisplayName("동시에 재고 차감을 요청해도, 재고는 정확히 차감된다.")
        @Test
        void deductAll_concurrent() throws InterruptedException {
            stockRepository.save(Stock.create(new StockCommand.Create(1L, 100L)));
            stockRepository.save(Stock.create(new StockCommand.Create(2L, 100L)));
            stockRepository.save(Stock.create(new StockCommand.Create(3L, 100L)));
            stockRepository.save(Stock.create(new StockCommand.Create(4L, 100L)));

            int threadCount = 10;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);


            for (int i = 0; i < threadCount; i++) {
                List<StockCommand.Deduct> commands = (i % 2 == 0)
                        ? List.of(new StockCommand.Deduct(1L, 10L), new StockCommand.Deduct(2L, 10L), new StockCommand.Deduct(3L, 10L), new StockCommand.Deduct(4L, 10L))
                        : List.of(new StockCommand.Deduct(4L, 10L), new StockCommand.Deduct(3L, 10L), new StockCommand.Deduct(2L, 10L), new StockCommand.Deduct(1L, 10L));
                executor.submit(() -> {
                    try {
                        stockService.deductAll(commands);
                    } catch (Exception e) {
                        System.out.println("Error : " + e.getMessage());
                    }
                    finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();

            Stock stock1 = stockRepository.findByProductId(1L).orElseThrow();
            assertThat(stock1.getQuantity().getValue()).isEqualTo(0L);
            Stock stock2 = stockRepository.findByProductId(2L).orElseThrow();
            assertThat(stock2.getQuantity().getValue()).isEqualTo(0L);
        }
    }
}
