package com.loopers.application.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
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
    private OrderProcessor orderProcessor;
    @Mock
    private PaymentProcessor paymentProcessor;

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

            CoreException thrown = assertThrows(CoreException.class, () -> orderFacade.order(new OrderCriteria.Order(1L, lines, delivery, null)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        }
    }
}
