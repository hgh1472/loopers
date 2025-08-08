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
}
