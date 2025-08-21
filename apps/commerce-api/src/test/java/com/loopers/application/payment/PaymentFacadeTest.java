package com.loopers.application.payment;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyList;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.point.InsufficientPointException;
import com.loopers.domain.stock.InsufficientStockException;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import java.util.List;
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
    PaymentFacade paymentFacade;
    @Mock
    RefundProcessor refundProcessor;
    @Mock
    SuccessProcessor successProcessor;
    @Mock
    PaymentService paymentService;
    @Mock
    OrderService orderService;
    @Mock
    CouponService couponService;

    @Nested
    @DisplayName("결제 완료 후,")
    class Success {

        @Test
        @DisplayName("성공 처리에서 실패하면, 사용한 쿠폰은 복원된다.")
        void restoreCoupon_whenResourceProcessorFails() {
            UUID orderId = UUID.randomUUID();
            PaymentCriteria.Success criteria = new PaymentCriteria.Success("transactionKey", orderId);
            OrderInfo orderInfo = new OrderInfo(orderId, 1L, 1L, "PENDING", List.of(new OrderInfo.Line(1L, 1L, new BigDecimal("100"))),
                    new OrderInfo.Delivery("hwang", "010-1234-5678", "서울시 강남구 역삼동", "123-456", "택배"),
                    new OrderInfo.Payment(BigDecimal.ZERO, BigDecimal.ZERO, 100L, BigDecimal.ZERO));
            given(orderService.get(new OrderCommand.Get(criteria.orderId())))
                    .willReturn(orderInfo);
            doThrow(new InsufficientStockException(ErrorType.CONFLICT, "재고가 부족합니다."))
                    .when(successProcessor)
                    .process(anyList(), any(), any(), any());

            paymentFacade.success(criteria);

            verify(refundProcessor, times(1))
                    .refund(orderInfo.userId(), orderInfo.couponId(), orderInfo.id(), criteria.transactionKey(), OrderCommand.Fail.Reason.OUT_OF_STOCK);
        }

        @Test
        @DisplayName("재고 차감을 실패할 경우, 재고 차감에 대한 환불이 진행된다.")
        void refund_whenInsufficientStock() {
            UUID orderId = UUID.randomUUID();
            PaymentCriteria.Success criteria = new PaymentCriteria.Success("transactionKey", orderId);
            OrderInfo orderInfo = new OrderInfo(orderId, 1L, 1L, "PENDING", List.of(new OrderInfo.Line(1L, 1L, new BigDecimal("100"))),
                    new OrderInfo.Delivery("hwang", "010-1234-5678", "서울시 강남구 역삼동", "123-456", "택배"),
                    new OrderInfo.Payment(BigDecimal.ZERO, BigDecimal.ZERO, 100L, BigDecimal.ZERO));
            given(orderService.get(new OrderCommand.Get(criteria.orderId())))
                    .willReturn(orderInfo);
            doThrow(new InsufficientStockException(ErrorType.CONFLICT, "재고가 부족합니다."))
                    .when(successProcessor)
                    .process(anyList(), any(), any(), any());

            paymentFacade.success(criteria);

            verify(refundProcessor, times(1))
                    .refund(orderInfo.userId(), orderInfo.couponId(), orderInfo.id(), criteria.transactionKey(), OrderCommand.Fail.Reason.OUT_OF_STOCK);
        }

        @Test
        @DisplayName("포인트가 부족할 경우, 포인트 사용에 대한 환불이 진행된다.")
        void refund_whenInsufficientPoint() {
            UUID orderId = UUID.randomUUID();
            PaymentCriteria.Success criteria = new PaymentCriteria.Success("transactionKey", orderId);
            OrderInfo orderInfo = new OrderInfo(orderId, 1L, 1L, "PENDING", List.of(new OrderInfo.Line(1L, 1L, new BigDecimal("100"))),
                    new OrderInfo.Delivery("hwang", "010-1234-5678", "서울시 강남구 역삼동", "123-456", "택배"),
                    new OrderInfo.Payment(BigDecimal.ZERO, BigDecimal.ZERO, 100L, BigDecimal.ZERO));
            given(orderService.get(new OrderCommand.Get(criteria.orderId())))
                    .willReturn(orderInfo);
            doThrow(new InsufficientPointException(ErrorType.CONFLICT, "포인트가 부족합니다."))
                    .when(successProcessor)
                    .process(anyList(), any(), any(), any());

            paymentFacade.success(criteria);

            verify(refundProcessor, times(1))
                    .refund(orderInfo.userId(), orderInfo.couponId(), orderInfo.id(), criteria.transactionKey(), OrderCommand.Fail.Reason.POINT_EXHAUSTED);
        }
    }
}
