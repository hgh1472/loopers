package com.loopers.application.order;

import com.loopers.domain.point.InsufficientPointException;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import com.loopers.domain.stock.InsufficientStockException;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ResourceProcessor {
    private final StockService stockService;
    private final PointService pointService;

    @Transactional
    public void deduct(List<StockCommand.Deduct> stockCommands, PointCommand.Use pointCommand)
            throws InsufficientStockException, InsufficientPointException {
        stockService.deductAll(stockCommands);
        if (pointCommand.amount() > 0) {
            pointService.use(pointCommand);
        }
    }
}
