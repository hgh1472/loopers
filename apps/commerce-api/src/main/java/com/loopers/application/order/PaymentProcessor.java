package com.loopers.application.order;

import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentProcessor {

    private final StockService stockService;
    private final PointService pointService;

    public void pay(PointCommand.Use pointCommand, List<StockCommand.Deduct> stockCommands) {
        pointService.use(pointCommand);
        stockService.deductAll(stockCommands);
    }
}
