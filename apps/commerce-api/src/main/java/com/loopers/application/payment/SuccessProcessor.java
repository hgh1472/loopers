package com.loopers.application.payment;

import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SuccessProcessor {

    private final StockService stockService;
    private final PointService pointService;
    private final PaymentService paymentService;
    private final OrderService orderService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process(List<StockCommand.Deduct> stockCommands, PointCommand.Use pointCommand,
                        PaymentCommand.Success paymentCommand, OrderCommand.Paid orderCommand) {
        stockService.deductAll(stockCommands);
        pointService.use(pointCommand);
        paymentService.success(paymentCommand);
        orderService.paid(orderCommand);
    }
}
