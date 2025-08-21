package com.loopers.application.payment;

import com.loopers.domain.coupon.CouponCommand;
import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RefundProcessor {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final CouponService couponService;

    @Transactional
    public void refund(Long userId, Long couponId, UUID orderId, String transactionKey, OrderCommand.Fail.Reason reason) {
        if (couponId != null) {
            couponService.restore(new CouponCommand.Restore(couponId, userId));
        }
        paymentService.refund(new PaymentCommand.Refund(transactionKey));
        orderService.fail(new OrderCommand.Fail(orderId, reason));
    }
}
