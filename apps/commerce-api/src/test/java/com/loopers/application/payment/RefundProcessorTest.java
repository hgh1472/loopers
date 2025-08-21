package com.loopers.application.payment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefundProcessorTest {
    @InjectMocks
    private RefundProcessor refundProcessor;
    @Mock
    private PaymentService paymentService;
    @Mock
    private OrderService orderService;
    @Mock
    private CouponService couponService;

    @Nested
    @DisplayName("환불 처리 시,")
    class Refund {
        @Test
        @DisplayName("쿠폰 ID가 null일 경우, 쿠폰 복원은 진행하지 않는다.")
        void doesNotRestoreCoupon_whenCouponIdIsNull() {
            Long userId = 1L;
            Long couponId = null;
            UUID orderId = UUID.randomUUID();
            String transactionKey = "transactionKey";
            OrderCommand.Fail.Reason reason = OrderCommand.Fail.Reason.OUT_OF_STOCK;

            refundProcessor.refund(userId, couponId, orderId, transactionKey, reason);

            verify(couponService, times(0))
                    .restore(any());
        }
    }
}
