package com.loopers.interfaces.event.coupon;

import com.loopers.application.order.OrderApplicationEvent;
import com.loopers.domain.coupon.CouponCommand;
import com.loopers.domain.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponEventListener {

    private final CouponService couponService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(OrderApplicationEvent.Created event) {
        log.info("Order Created Event: {}", event);
        if (event.couponId() == null) {
            return;
        }
        CouponCommand.Use command = new CouponCommand.Use(event.couponId(), event.userId());
        couponService.use(command);
    }
}
