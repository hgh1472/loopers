package com.loopers.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Nested
    @DisplayName("주문 생성 시,")
    class Create {

        @DisplayName("주문자 ID가 null일 경우, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenUserIdIsNull() {
            OrderCommand.Delivery delivery = new OrderCommand.Delivery("황건하", "010-1234-5678", "서울특별시 강남구 강남대로 지하396", "강남역 지하 XX", "요구사항");
            List<OrderCommand.Line> lines = List.of(new OrderCommand.Line(1L, 2L, new BigDecimal("3000")));
            OrderCommand.Order cmd = new OrderCommand.Order(null, lines, delivery, new BigDecimal("6000"), new BigDecimal("6000"));

            CoreException thrown = assertThrows(CoreException.class, () -> Order.of(cmd));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "사용자 ID가 존재하지 않습니다."));
        }

        @DisplayName("총 금액과 결제 금액이 저장된다.")
        @Test
        void order_withOrderPayment() {
            OrderCommand.Delivery delivery = new OrderCommand.Delivery("황건하", "010-1234-5678", "서울특별시 강남구 강남대로 지하396", "강남역 지하 XX", "요구사항");
            List<OrderCommand.Line> lines = List.of(new OrderCommand.Line(1L, 2L, new BigDecimal("3000")));
            OrderCommand.Order cmd = new OrderCommand.Order(1L, lines, delivery, new BigDecimal("6000"), new BigDecimal("4000"));

            Order order = Order.of(cmd);

            assertThat(order.getOrderPayment().getPaymentAmount()).isEqualTo(new BigDecimal("4000"));
            assertThat(order.getOrderPayment().getOriginalAmount()).isEqualTo(new BigDecimal("6000"));
        }

        @DisplayName("주문 내부에 주문 항목들이 존재한다.")
        @Test
        void order_withOrderLines() {
            OrderCommand.Delivery delivery = new OrderCommand.Delivery("황건하", "010-1234-5678", "서울특별시 강남구 강남대로 지하396", "강남역 지하 XX", "요구사항");
            List<OrderCommand.Line> lines = List.of(new OrderCommand.Line(1L, 2L, new BigDecimal("3000")));
            OrderCommand.Order cmd = new OrderCommand.Order(1L, lines, delivery, new BigDecimal("3000"), new BigDecimal("3000"));

            Order order = Order.of(cmd);

            assertThat(order.getOrderLines()).hasSize(1);
            assertThat(order.getOrderLines().get(0).getProductId()).isEqualTo(1L);
            assertThat(order.getOrderLines().get(0).getQuantity()).isEqualTo(2L);
            assertThat(order.getOrderLines().get(0).getAmount()).isEqualTo(new BigDecimal("3000"));
        }
    }

    @DisplayName("주문 항목 추가 시, 주문 항목이 null일 경우, BAD_REQUEST 예외를 발생시킨다.")
    @Test
    void throwBadRequestException_whenOrderLineIsNull() {
        OrderCommand.Delivery delivery = new OrderCommand.Delivery("황건하", "010-1234-5678", "서울특별시 강남구 강남대로 지하396", "강남역 지하 XX", "요구사항");
        List<OrderCommand.Line> lines = List.of(new OrderCommand.Line(1L, 2L, new BigDecimal("3000")));
        OrderCommand.Order cmd = new OrderCommand.Order(1L, lines, delivery, new BigDecimal("6000"), new BigDecimal("6000"));

        Order order = Order.of(cmd);

        CoreException thrown = assertThrows(CoreException.class, () -> order.addLine(null));

        assertThat(thrown)
                .usingRecursiveComparison()
                .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "주문 항목이 존재하지 않습니다."));
    }
}
