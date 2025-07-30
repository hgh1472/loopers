package com.loopers.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrderPaymentTest {

    @Nested
    @DisplayName("결제 정보 생성 시,")
    class Create {

        @DisplayName("결제 금액이 null이면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenTotalAmountIsNull() {
            CoreException thrown = assertThrows(CoreException.class, () -> new OrderPayment(null));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "결제 금액이 존재하지 않습니다."));
        }

        @DisplayName("결제 금액이 0 미만이면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenTotalAmountIsNegative() {
            CoreException thrown = assertThrows(CoreException.class, () -> new OrderPayment(BigDecimal.valueOf(-1)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "결제 금액은 0 이상이어야 합니다."));
        }
    }

    @Nested
    @DisplayName("결제 금액을 추가할 때,")
    class Add {
        @DisplayName("추가 금액이 null이면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenAmountIsNull() {
            OrderPayment orderPayment = new OrderPayment(BigDecimal.valueOf(1000));

            CoreException thrown = assertThrows(CoreException.class, () -> orderPayment.add(null));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "추가 금액은 0 이상이어야 합니다."));
        }

        @DisplayName("추가 금액이 0 미만이면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenAmountIsNegative() {
            OrderPayment orderPayment = new OrderPayment(BigDecimal.valueOf(1000));

            CoreException thrown = assertThrows(CoreException.class, () -> orderPayment.add(BigDecimal.valueOf(-1)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "추가 금액은 0 이상이어야 합니다."));
        }
    }
}
