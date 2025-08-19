package com.loopers.application.payment;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyList;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.loopers.domain.coupon.CouponCommand;
import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import java.util.List;
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
    PaymentFacade paymentFacade;
    @Mock
    PaymentService paymentService;
    @Mock
    OrderService orderService;
    @Mock
    SuccessProcessor successProcessor;
    @Mock
    CouponService couponService;

    @Nested
    @DisplayName("결제 완료 후,")
    class Success {

        @Test
        @DisplayName("성공 처리에서 실패하면, 사용한 쿠폰은 복원된다.")
        void restoreCoupon_whenResourceProcessorFails() {
            PaymentCriteria.Success criteria = new PaymentCriteria.Success("transactionKey", 1L);
            OrderInfo orderInfo = new OrderInfo(1L, 1L, 1L, "PENDING", List.of(new OrderInfo.Line(1L, 1L, new BigDecimal("100"))),
                    new OrderInfo.Delivery("hwang", "010-1234-5678", "서울시 강남구 역삼동", "123-456", "택배"),
                    new OrderInfo.Payment(BigDecimal.ZERO, BigDecimal.ZERO, 100L, BigDecimal.ZERO));
            given(orderService.get(new OrderCommand.Get(criteria.orderId())))
                    .willReturn(orderInfo);
            doThrow(new CoreException(ErrorType.CONFLICT, "재고가 부족합니다."))
                    .when(successProcessor)
                    .process(anyList(), any(), any(), any());

            paymentFacade.success(criteria);

            verify(couponService, times(1)).restore(new CouponCommand.Restore(1L, 1L));
        }

        @Test
        @DisplayName("성공 처리에서 실패하면, 결제는 환불된다.")
        void refundPayment_whenResourceProcessorFails() {
            PaymentCriteria.Success criteria = new PaymentCriteria.Success("transactionKey", 1L);
            OrderInfo orderInfo = new OrderInfo(1L, 1L, 1L, "PENDING", List.of(new OrderInfo.Line(1L, 1L, new BigDecimal("100"))),
                    new OrderInfo.Delivery("hwang", "010-1234-5678", "서울시 강남구 역삼동", "123-456", "택배"),
                    new OrderInfo.Payment(BigDecimal.ZERO, BigDecimal.ZERO, 100L, BigDecimal.ZERO));
            given(orderService.get(new OrderCommand.Get(criteria.orderId())))
                    .willReturn(orderInfo);
            doThrow(new CoreException(ErrorType.CONFLICT, "포인트 부족"))
                    .when(successProcessor)
                    .process(anyList(), any(), any(), any());

            paymentFacade.success(criteria);

            verify(paymentService, times(1)).refund(new PaymentCommand.Refund(criteria.transactionKey()));
        }

        @Test
        @DisplayName("성공 처리에서 실패하면, 주문은 실패로 처리된다.")
        void failOrder_whenResourceProcessorFails() {
            PaymentCriteria.Success criteria = new PaymentCriteria.Success("transactionKey", 1L);
            OrderInfo orderInfo = new OrderInfo(1L, 1L, 1L, "PENDING", List.of(new OrderInfo.Line(1L, 1L, new BigDecimal("100"))),
                    new OrderInfo.Delivery("hwang", "010-1234-5678", "서울시 강남구 역삼동", "123-456", "택배"),
                    new OrderInfo.Payment(BigDecimal.ZERO, BigDecimal.ZERO, 100L, BigDecimal.ZERO));
            given(orderService.get(new OrderCommand.Get(criteria.orderId())))
                    .willReturn(orderInfo);
            doThrow(new CoreException(ErrorType.CONFLICT, "포인트가 부족합니다."))
                    .when(successProcessor)
                    .process(anyList(), any(), any(), any());

            paymentFacade.success(criteria);

            verify(orderService, times(1)).fail(new OrderCommand.Fail(orderInfo.id(), OrderCommand.Fail.Reason.POINT_EXHAUSTED));
        }
    }
}
