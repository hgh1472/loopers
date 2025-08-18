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

        @DisplayName("총 금액이 null이면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenOriginalAmountIsNull() {
            CoreException thrown = assertThrows(CoreException.class, () -> new OrderPayment(null, new BigDecimal("100"),  0L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "총 금액이 존재하지 않습니다."));
        }

        @DisplayName("총 금액이 0 미만이면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenOriginalAmountIsNegative() {
            CoreException thrown = assertThrows(CoreException.class, () -> new OrderPayment(BigDecimal.valueOf(-1), BigDecimal.valueOf(1000), 0L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "총 금액은 0 이상이어야 합니다."));
        }

        @DisplayName("할인 금액이 null이면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenDiscountAmountIsNull() {
            CoreException thrown = assertThrows(CoreException.class, () -> new OrderPayment(new BigDecimal("1000"), null, 0L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "할인 금액에 대한 정보가 존재하지 않습니다."));
        }

        @DisplayName("할인 금액이 0 미만이면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenDiscountAmountIsNegative() {
            CoreException thrown = assertThrows(CoreException.class, () -> new OrderPayment(new BigDecimal("1000"), BigDecimal.valueOf(-1), 0L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "할인 금액은 0 이상이어야 합니다."));
        }

        @DisplayName("사용할 포인트가 null이면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenPointAmountIsNull() {
            CoreException thrown = assertThrows(CoreException.class, () -> new OrderPayment(new BigDecimal("1000"), new BigDecimal("100"), null));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "사용한 포인트에 대한 정보가 존재하지 않습니다."));
        }

        @DisplayName("사용할 포인트가 0 미만이면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenPointAmountIsNegative() {
            CoreException thrown = assertThrows(CoreException.class, () -> new OrderPayment(new BigDecimal("1000"), new BigDecimal("100"), -1L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "사용한 포인트는 0 이상이어야 합니다."));
        }

        @DisplayName("정상적인 결제 정보를 생성한다.")
        @Test
        void createOrderPaymentSuccessfully() {
            BigDecimal originalAmount = new BigDecimal("1000");
            BigDecimal discountAmount = new BigDecimal("100");
            Long pointAmount = 50L;

            OrderPayment orderPayment = new OrderPayment(originalAmount, discountAmount, pointAmount);

            assertThat(orderPayment.getOriginalAmount()).isEqualTo(originalAmount);
            assertThat(orderPayment.getDiscountAmount()).isEqualTo(discountAmount);
            assertThat(orderPayment.getPointAmount()).isEqualTo(pointAmount);
            assertThat(orderPayment.getPaymentAmount()).isEqualTo(originalAmount.subtract(discountAmount).subtract(BigDecimal.valueOf(pointAmount)));
        }
    }
}
