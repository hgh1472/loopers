package com.loopers.application.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.point.InsufficientPointException;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.stock.InsufficientStockException;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {

    @InjectMocks
    private OrderFacade orderFacade;
    @Mock
    private OrderApplicationEventPublisher orderApplicationEventPublisher;
    @Mock
    private AmountProcessor amountProcessor;
    @Mock
    private ResourceProcessor resourceProcessor;
    @Mock
    private UserService userService;
    @Mock
    private ProductService productService;
    @Mock
    private OrderService orderService;

    @Nested
    @DisplayName("주문 시,")
    class Orders {

        @DisplayName("사용자가 없는 경우, NOT_FOUND 예외를 발생시킨다.")
        @Test
        void throwNotFoundException_whenUserNotFound() {
            given(userService.findUser(new UserCommand.Find(1L)))
                    .willReturn(null);
            List<OrderCriteria.Line> lines = List.of(new OrderCriteria.Line(1L, 1L));
            OrderCriteria.Delivery delivery = new OrderCriteria.Delivery("주문자", "010-1234-5678", "서울시 강남구 역삼동 123-45", "서울시 강남구 역삼동 123-45", "요청사항");

            CoreException thrown = assertThrows(CoreException.class, () -> orderFacade.order(new OrderCriteria.Order(1L, lines, delivery, null, 0L)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        }


        @DisplayName("주문 상품 중, 구매 불가능한 상품이 있는 경우, NOT_FOUND 예외가 발생한다.")
        @Test
        void throwNotFoundException_whenNotPurchasable() {
            List<OrderCriteria.Line> lines = List.of(
                    new OrderCriteria.Line(1L, 1L),
                    new OrderCriteria.Line(2L, 2L)
            );
            OrderCriteria.Delivery delivery = new OrderCriteria.Delivery("주문자", "010-1234-5678",
                    "서울시 강남구 역삼동 123-45", "서울시 강남구 역삼동 123-45", "요청사항");
            given(userService.findUser(new UserCommand.Find(1L)))
                    .willReturn(new UserInfo(1L, "user1", "hgh1472@loopers.com", LocalDate.now(), "MALE"));
            given(productService.getPurchasableProducts(new ProductCommand.Purchasable(Set.of(1L, 2L))))
                    .willReturn(List.of(new ProductInfo(1L, 2L, "상품1", BigDecimal.valueOf(1000), "ON_SALE")));

            OrderCriteria.Order criteria = new OrderCriteria.Order(1L, lines, delivery, 1L, 0L);

            CoreException thrown = Assert.assertThrows(CoreException.class, () -> orderFacade.order(criteria));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "주문에 필요한 상품 정보를 찾을 수 없습니다."));
        }
    }

    @Nested
    @DisplayName("주문 결제 완료 처리 시,")
    class SucceedPayment {

        @Test
        @DisplayName("차감 중 InsufficientStockException이 발생하면, 환불 이벤트를 발행한다.")
        void publishRefundEvent_whenInsufficientStockException() throws InsufficientStockException, InsufficientPointException {
            UUID orderId = UUID.randomUUID();
            OrderCriteria.Success criteria = new OrderCriteria.Success(orderId, "TX-KEY");
            OrderInfo.Payment payment = new OrderInfo.Payment(BigDecimal.valueOf(2000L), BigDecimal.valueOf(100L), 500L, BigDecimal.valueOf(1400L));
            List<OrderInfo.Line> lines = List.of(new OrderInfo.Line(1L, 1L, BigDecimal.valueOf(1000L)));
            given(orderService.get(new OrderCommand.Get(orderId)))
                    .willReturn(new OrderInfo(orderId, 1L, 1L, "PENDING", lines, null, payment));

            doThrow(InsufficientStockException.class)
                    .when(resourceProcessor)
                    .deduct(anyList(), any());

            orderFacade.succeedPayment(criteria);

            verify(orderApplicationEventPublisher, times(1))
                    .publish(new OrderApplicationEvent.Refund(orderId, 1L, "TX-KEY", 1L, OrderApplicationEvent.Refund.Reason.OUT_OF_STOCK));
        }

        @Test
        @DisplayName("차감 중 InsufficientPointException이 발생하면, 환불 이벤트를 발행한다.")
        void publishRefundEvent_whenInsufficientPointException() throws InsufficientStockException, InsufficientPointException {
            UUID orderId = UUID.randomUUID();
            OrderCriteria.Success criteria = new OrderCriteria.Success(orderId, "TX-KEY");
            OrderInfo.Payment payment = new OrderInfo.Payment(BigDecimal.valueOf(2000L), BigDecimal.valueOf(100L), 500L, BigDecimal.valueOf(1400L));
            List<OrderInfo.Line> lines = List.of(new OrderInfo.Line(1L, 1L, BigDecimal.valueOf(1000L)));
            given(orderService.get(new OrderCommand.Get(orderId)))
                    .willReturn(new OrderInfo(orderId, 1L, 1L, "PENDING", lines, null, payment));

            doThrow(InsufficientPointException.class)
                    .when(resourceProcessor)
                    .deduct(anyList(), any());

            orderFacade.succeedPayment(criteria);

            verify(orderApplicationEventPublisher, times(1))
                    .publish(new OrderApplicationEvent.Refund(orderId, 1L, "TX-KEY", 1L, OrderApplicationEvent.Refund.Reason.POINT_EXHAUSTED));
        }
    }
}
