package com.loopers.application.payment;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.loopers.domain.coupon.CouponCommand;
import com.loopers.domain.coupon.CouponService;
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
class PaymentFacadeTest {
    @InjectMocks
    private PaymentFacade paymentFacade;
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
        @DisplayName("쿠폰을 사용한 경우, 쿠폰 복원 요청을 한다.")
        void refund_whenCouponUsed() {
            UUID orderId = UUID.randomUUID();
            PaymentCriteria.Refund criteria = new PaymentCriteria.Refund(
                    1L, 1L, orderId, "TX-KEY", PaymentCriteria.Refund.Reason.OUT_OF_STOCK);

            paymentFacade.refund(criteria);

            verify(couponService, times(1))
                    .restore(new CouponCommand.Restore(criteria.couponId(), criteria.userId()));
        }
    }
}
