package com.loopers.application.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.loopers.domain.order.OrderService;
import com.loopers.domain.point.PointService;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.stock.StockService;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {

    @InjectMocks
    private OrderFacade orderFacade;
    @Mock
    private UserService userService;
    @Mock
    private ProductService productService;
    @Mock
    private OrderService orderService;
    @Mock
    private StockService stockService;
    @Mock
    private PointService pointService;

    @Nested
    @DisplayName("주문 시,")
    class Orders {

        @DisplayName("상품이 없는 경우, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenNoProducts() {
            given(userService.findUser(new UserCommand.Find(1L)))
                    .willReturn(new UserInfo(1L, "login", "hgh1472@loopers.im", LocalDate.of(1999, 6, 23), "MALE"));
            given(productService.getProducts(new ProductCommand.GetProducts(Set.of(1L))))
                    .willReturn(List.of(new ProductInfo(1L, 1L, "상품 1", new BigDecimal("1000.00"), "HOLD")));
            List<OrderCriteria.Line> lines = List.of(new OrderCriteria.Line(1L, 1L));
            OrderCriteria.Delivery delivery = new OrderCriteria.Delivery("주문자", "010-1234-5678", "서울시 강남구 역삼동 123-45", "서울시 강남구 역삼동 123-45", "요청사항");
            CoreException thrown = assertThrows(CoreException.class,
                    () -> orderFacade.order(new OrderCriteria.Order(1L, lines, delivery)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "상품이 판매 중이 아닙니다."));
        }
    }

}
