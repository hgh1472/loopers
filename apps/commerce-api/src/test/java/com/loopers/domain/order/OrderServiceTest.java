package com.loopers.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @DisplayName("주문 조회 시, 해당 주문이 없는 경우, NOT_FOUND 예외를 발생시킨다.")
    @Test
    void throwNotFoundException_whenOrderDoesNotExist() {
        OrderCommand.Get command = new OrderCommand.Get(1L);
        given(orderRepository.findById(1L))
                .willReturn(java.util.Optional.empty());

        CoreException thrown = assertThrows(CoreException.class, () -> {
            orderService.get(command);
        });

        assertThat(thrown)
                .usingRecursiveComparison()
                .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 주문입니다."));
    }
}
