package com.loopers.application.order;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.loopers.domain.point.InsufficientPointException;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.stock.InsufficientStockException;
import com.loopers.domain.stock.Stock;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockRepository;
import com.loopers.utils.DatabaseCleanUp;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ResourceProcessorTest {

    @Autowired
    private ResourceProcessor resourceProcessor;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("결제 완료 처리 시,")
    class Success {

        @Test
        @DisplayName("재고가 부족한 경우, InsufficientStockException을 발생시킨다.")
        void restoreCoupon_whenResourceProcessorFails() throws InsufficientStockException, InsufficientPointException {
            Stock stock = Stock.create(new StockCommand.Create(1L, 0L));
            stockRepository.save(stock);
            List<StockCommand.Deduct> commands = List.of(new StockCommand.Deduct(1L, 1L));
            PointCommand.Use pointCommand = new PointCommand.Use(1L, 100L);

            assertThatThrownBy(() -> resourceProcessor.deduct(commands, pointCommand))
                    .isInstanceOf(InsufficientStockException.class);
        }

        @Test
        @DisplayName("포인트가 부족한 경우, InsufficientPointException을 발생시킨다.")
        void restoreCoupon_whenPointInsufficient() {
            Stock stock = Stock.create(new StockCommand.Create(1L, 10L));
            stockRepository.save(stock);
            Point point = Point.from(1L);
            point.charge(50L);
            pointRepository.save(point);
            List<StockCommand.Deduct> commands = List.of(new StockCommand.Deduct(1L, 1L));
            PointCommand.Use pointCommand = new PointCommand.Use(1L, 100L);

            assertThatThrownBy(() -> resourceProcessor.deduct(commands, pointCommand))
                    .isInstanceOf(InsufficientPointException.class);
        }
    }
}
