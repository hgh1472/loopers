package com.loopers.domain.order;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class OrderTest {

    @Nested
    @DisplayName("주문 생성 시,")
    class Create {

        @DisplayName("주문자 ID가 null일 경우, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenUserIdIsNull() {
            OrderCommand.Delivery delivery = new OrderCommand.Delivery("황건하", "010-1234-5678", "서울특별시 강남구 강남대로 지하396", "강남역 지하 XX", "요구사항");

            CoreException thrown = assertThrows(CoreException.class, () -> Order.of(null, delivery));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "사용자 ID가 존재하지 않습니다."));
        }
    }

    @DisplayName("주문 항목 추가 시, 주문 항목이 null일 경우, BAD_REQUEST 예외를 발생시킨다.")
    @Test
    void throwBadRequestException_whenOrderLineIsNull() {
        OrderCommand.Delivery delivery = new OrderCommand.Delivery("황건하", "010-1234-5678", "서울특별시 강남구 강남대로 지하396", "강남역 지하 XX", "요구사항");
        Order order = Order.of(1L, delivery);

        CoreException thrown = assertThrows(CoreException.class, () -> order.addLine(null));

        assertThat(thrown)
                .usingRecursiveComparison()
                .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "주문 항목이 존재하지 않습니다."));
    }

    @DisplayName("주문 항목 추가 시, 결제 금액이 증가한다.")
    @Test
    void increasePaymentAmount_whenAddLine() {
        OrderCommand.Delivery delivery = new OrderCommand.Delivery("황건하", "010-1234-5678", "서울특별시 강남구 강남대로 지하396", "강남역 지하 XX", "요구사항");
        Order order = Order.of(1L, delivery);

        order.addLine(OrderLine.from(new OrderCommand.Line(1L, 2L, new BigDecimal("3000"))));

        assertThat(order.getOrderPayment().getPaymentAmount()).isEqualTo(new BigDecimal("6000"));
    }
}
