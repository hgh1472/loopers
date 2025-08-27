package com.loopers.application.payment;

import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentInfo;
import com.loopers.domain.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final CouponService couponService;

    @Transactional
    public PaymentResult pay(PaymentCriteria.Pay criteria) {
        OrderInfo orderInfo = orderService.get(new OrderCommand.Get(criteria.orderId()));
        PaymentCommand.Pay command = new PaymentCommand.Pay(
                orderInfo.payment().paymentAmount(), orderInfo.id(), criteria.cardType(), criteria.cardNo());
        PaymentInfo paymentInfo = paymentService.pay(command);
        if (paymentInfo.status() == Payment.Status.PENDING) {
            orderService.pending(new OrderCommand.Pending(orderInfo.id()));
        }
        return PaymentResult.from(paymentInfo);
    }
}
