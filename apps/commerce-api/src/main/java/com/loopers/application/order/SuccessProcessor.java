package com.loopers.application.order;

import com.loopers.application.payment.RefundProcessor;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.point.InsufficientPointException;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import com.loopers.domain.stock.InsufficientStockException;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockCommand.Deduct;
import com.loopers.domain.stock.StockService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SuccessProcessor {
    private final OrderService orderService;
    private final StockService stockService;
    private final PointService pointService;
    private final RefundProcessor refundProcessor;

    @Transactional
    public void success(OrderCriteria.Success criteria) {
        OrderInfo orderInfo = orderService.get(new OrderCommand.Get(criteria.orderId()));

        List<Deduct> stockCommands = orderInfo.lines().stream()
                .map(line -> new StockCommand.Deduct(line.productId(), line.quantity()))
                .toList();
        PointCommand.Use pointCommand = new PointCommand.Use(orderInfo.userId(), orderInfo.payment().pointAmount());
        OrderCommand.Paid orderCommand = new OrderCommand.Paid(orderInfo.id());
        try {
            stockService.deductAll(stockCommands);
            if (pointCommand.amount() > 0) {
                pointService.use(pointCommand);
            }
            orderService.paid(orderCommand);
        } catch (InsufficientPointException e) {
            refundProcessor.refund(orderInfo.userId(), orderInfo.couponId(), orderInfo.id(), criteria.transactionKey(),
                    OrderCommand.Fail.Reason.POINT_EXHAUSTED);
        } catch (InsufficientStockException e) {
            refundProcessor.refund(orderInfo.userId(), orderInfo.couponId(), orderInfo.id(), criteria.transactionKey(),
                    OrderCommand.Fail.Reason.OUT_OF_STOCK);
        }
    }
}
