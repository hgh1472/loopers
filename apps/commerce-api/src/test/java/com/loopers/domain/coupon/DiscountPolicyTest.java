package com.loopers.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DiscountPolicyTest {

    @Nested
    @DisplayName("할인 정책 생성 시,")
    class Create {
        @DisplayName("할인 금액 또는 할인율이 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwBadRequestException_whenValueIsNull() {
            BigDecimal value = null;
            DiscountPolicy.Type type = DiscountPolicy.Type.FIXED;

            CoreException thrown = assertThrows(CoreException.class, () -> new DiscountPolicy(value, type));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "할인 금액 또는 할인율은 필수입니다."));
        }

        @DisplayName("쿠폰 유형이 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwBadRequestException_whenTypeIsNull() {
            BigDecimal value = BigDecimal.TEN;
            DiscountPolicy.Type type = null;

            CoreException thrown = assertThrows(CoreException.class, () -> new DiscountPolicy(value, type));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "쿠폰 유형은 필수입니다."));
        }

        @DisplayName("정률 쿠폰의 할인율이 0 이하이거나 1보다 크면, BAD_REQUEST 예외가 발생한다.")
        @ValueSource(strings = {"-1", "0", "1.01"})
        @ParameterizedTest
        void throwBadRequestException_whenRateIsZeroOrNegative(String value) {
            BigDecimal discountValue = new BigDecimal(value);
            DiscountPolicy.Type type = DiscountPolicy.Type.RATE;

            CoreException thrown = assertThrows(CoreException.class, () -> new DiscountPolicy(discountValue, type));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "할인율은 0보다 크고 1 이하이어야 합니다."));
        }

        @DisplayName("정액 쿠폰의 할인 금액이 0 이하이면, BAD_REQUEST 예외가 발생한다.")
        @ValueSource(strings = {"-1", "0"})
        @ParameterizedTest
        void throwBadRequestException_whenFixedDiscountIsZeroOrNegative(String value) {
            BigDecimal discountValue = new BigDecimal(value);
            DiscountPolicy.Type type = DiscountPolicy.Type.FIXED;

            CoreException thrown = assertThrows(CoreException.class, () -> new DiscountPolicy(discountValue, type));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "고정 할인 금액 은 0보다 커야 합니다."));
        }
    }

    @Nested
    @DisplayName("할인 적용 시,")
    class Discount {
        @DisplayName("정률 쿠폰은 금액에 할인율을 곱한 값을 반환한다.")
        @Test
        void returnDiscountedAmount_whenTypeIsRate() {
            BigDecimal amount = new BigDecimal("1000");
            BigDecimal discountValue = new BigDecimal("0.1");
            DiscountPolicy.Type type = DiscountPolicy.Type.RATE;
            DiscountPolicy discountPolicy = new DiscountPolicy(discountValue, type);

            BigDecimal discountedAmount = discountPolicy.discount(amount);

            assertThat(discountedAmount).isEqualTo(new BigDecimal("900.00"));
        }

        @DisplayName("정액 쿠폰은 금액에서 할인 금액을 뺀 값을 반환한다.")
        @Test
        void returnDiscountedAmount_whenTypeIsFixed() {
            BigDecimal amount = new BigDecimal("1000");
            BigDecimal discountValue = new BigDecimal("200");
            DiscountPolicy.Type type = DiscountPolicy.Type.FIXED;
            DiscountPolicy discountPolicy = new DiscountPolicy(discountValue, type);

            BigDecimal discountedAmount = discountPolicy.discount(amount);

            assertThat(discountedAmount).isEqualTo(new BigDecimal("800.00"));
        }
    }
}
