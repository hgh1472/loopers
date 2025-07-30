package com.loopers.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.domain.order.OrderCommand.Line;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class OrderLineTest {

    @Nested
    @DisplayName("주문 항목 생성 시,")
    class Create {

        @DisplayName("주문 항목이 null이면, BAD_REQUEST 예외를 반환한다.")
        @Test
        void throwBadRequestException_whenLineIsNull() {
            CoreException thrown = assertThrows(CoreException.class, () -> OrderLine.from(null));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "주문 항목이 존재하지 않습니다."));
        }

        @DisplayName("상품 ID가 비어있으면, BAD_REQUEST 예외를 반환한다.")
        @Test
        void throwBadRequestException_whenProductIdIsNull() {
            CoreException thrown = assertThrows(CoreException.class, () -> OrderLine.from(new Line(null, 1L, BigDecimal.valueOf(1000L))));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "상품 ID는 필수입니다."));
        }

        @DisplayName("수량이 1개 미만이면, BAD_REQUEST 예외를 반환한다.")
        @ValueSource(longs = {0, -1})
        @ParameterizedTest(name = "주문 수량 = {0}")
        void throwBadRequestException_whenQuantityIsLessThanOne(Long quantity) {
            CoreException thrown = assertThrows(CoreException.class, () -> OrderLine.from(new Line(1L, quantity, BigDecimal.valueOf(1000L))));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "수량은 1 이상이어야 합니다."));
        }

        @DisplayName("수량이 1개 이상이면, 생성에 성공한다.")
        @Test
        void createOrderLine_withPositiveQuantity() {
            Line line = new Line(1L, 1L, BigDecimal.valueOf(1000L));
            OrderLine orderLine = OrderLine.from(line);

            assertThat(orderLine.getProductId()).isEqualTo(line.productId());
            assertThat(orderLine.getQuantity()).isEqualTo(line.quantity());
            assertThat(orderLine.getAmount()).isEqualTo(line.price());
        }

        @DisplayName("가격이 음수라면, BAD_REQUEST 예외를 반환한다.")
        @Test
        void throwBadRequestException_whenPriceIsNegative() {
            CoreException thrown = assertThrows(CoreException.class, () -> OrderLine.from(new Line(1L, 1L, BigDecimal.valueOf(-1L))));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "가격은 0 이상이어야 합니다."));
        }

        @DisplayName("가격이 0이라면, 생성에 성공한다.")
        @Test
        void createOrderLine_withZeroPrice() {
            Line line = new Line(1L, 1L, BigDecimal.ZERO);
            OrderLine orderLine = OrderLine.from(line);

            assertThat(orderLine.getProductId()).isEqualTo(line.productId());
            assertThat(orderLine.getQuantity()).isEqualTo(line.quantity());
            assertThat(orderLine.getAmount()).isEqualTo(line.price());
        }
    }

    @Nested
    @DisplayName("주문 항목 목록 생성 시,")
    class CreateList {

        @DisplayName("주문 항목 목록이 비어있으면, BAD_REQUEST 예외를 반환한다.")
        @NullAndEmptySource
        @ParameterizedTest(name = "주문 항목 목록 = {0}")
        void throwBadRequestException_whenLinesIsNull(List<OrderCommand.Line> lines) {
            CoreException thrown = assertThrows(CoreException.class, () -> OrderLine.of(lines));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "주문 항목이 존재하지 않습니다."));
        }
    }
}
