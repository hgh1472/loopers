package com.loopers.application.payment;

import com.loopers.domain.coupon.CouponCommand;
import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.point.InsufficientPointException;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.stock.InsufficientStockException;
import com.loopers.domain.stock.StockCommand;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final CouponService couponService;
    private final SuccessProcessor successProcessor;
    private final RefundProcessor refundProcessor;

    @Transactional
    public void success(PaymentCriteria.Success criteria) {
        OrderInfo orderInfo = orderService.get(new OrderCommand.Get(criteria.orderId()));
        List<StockCommand.Deduct> stockCommands = orderInfo.lines().stream()
                .map(line -> new StockCommand.Deduct(line.productId(), line.quantity()))
                .toList();
        PointCommand.Use pointCommand = new PointCommand.Use(orderInfo.userId(), orderInfo.payment().pointAmount());
        PaymentCommand.Success paymentCommand = new PaymentCommand.Success(criteria.transactionKey());
        OrderCommand.Paid orderCommand = new OrderCommand.Paid(orderInfo.id());
        try {
            successProcessor.process(stockCommands, pointCommand, paymentCommand, orderCommand);
        } catch (InsufficientPointException e) {
            refundProcessor.refund(orderInfo.userId(), orderInfo.couponId(), orderInfo.id(), criteria.transactionKey(),
                    OrderCommand.Fail.Reason.POINT_EXHAUSTED);
        } catch (InsufficientStockException e) {
            refundProcessor.refund(orderInfo.userId(), orderInfo.couponId(), orderInfo.id(), criteria.transactionKey(),
                    OrderCommand.Fail.Reason.OUT_OF_STOCK);
        }
    }

    @Transactional
    public void fail(PaymentCriteria.Fail criteria) {
        OrderInfo orderInfo = orderService.get(new OrderCommand.Get(criteria.orderId()));
        if (orderInfo.couponId() != null) {
            couponService.restore(new CouponCommand.Restore(orderInfo.couponId(), orderInfo.userId()));
        }
        paymentService.fail(new PaymentCommand.Fail(criteria.transactionKey(), criteria.reason()));
        orderService.fail(new OrderCommand.Fail(orderInfo.id(), OrderCommand.Fail.Reason.PAYMENT_FAILED));
    }
}
