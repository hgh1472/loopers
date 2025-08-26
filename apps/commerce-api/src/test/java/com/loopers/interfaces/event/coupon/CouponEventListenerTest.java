package com.loopers.interfaces.event.coupon;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.verify;

import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.order.OrderEvent;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CouponEventListenerTest {

    @InjectMocks
    private CouponEventListener couponEventListener;
    @Mock
    private CouponService couponService;

    @Nested
    @DisplayName("주문 생성 이벤트 처리 시,")
    class OrderEventCreated {

        @Test
        @DisplayName("쿠폰 ID가 null일 경우, 쿠폰을 사용하지 않는다.")
        void doNotUseCoupon_whenCouponIdIsNull() {
            couponEventListener.handle(new OrderEvent.Created(UUID.randomUUID(), 1L, null));

            verify(couponService, never()).use(any());
        }
    }
}
