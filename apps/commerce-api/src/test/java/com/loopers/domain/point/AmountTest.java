package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AmountTest {

    @DisplayName("0보다 적은 값으로 포인트 생성 시, BAD_REQUEST 예외를 발생시킨다.")
    @Test
    void throwBadRequestException_whenInitializeNonPositivePoint() {
        CoreException thrown = assertThrows(CoreException.class, () -> new Amount(-1L));

        assertThat(thrown)
                .usingRecursiveComparison()
                .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "포인트는 0 이상이어야 합니다."));
    }

    @Nested
    @DisplayName("포인트 충전 시,")
    class Charge {
        @DisplayName("amount가 null이면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenAmountNull() {
            Amount amount = new Amount(0L);

            CoreException thrown = assertThrows(CoreException.class, () -> amount.charge(null));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "0 이하의 포인트는 충전할 수 없습니다."));
        }

        @DisplayName("amount가 음수면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenNegativeAmount() {
            Amount amount = new Amount(0L);

            CoreException thrown = assertThrows(CoreException.class, () -> amount.charge(-1L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "0 이하의 포인트는 충전할 수 없습니다."));
        }
    }

    @Nested
    @DisplayName("포인트 사용 시,")
    class Use {
        @DisplayName("amount가 null이면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenAmountNull() {
            Amount amount = new Amount(0L);

            CoreException thrown = assertThrows(CoreException.class, () -> amount.use(null));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "0 이하의 포인트는 사용할 수 없습니다."));
        }

        @DisplayName("amount가 음수면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenNegativeAmount() {
            Amount amount = new Amount(0L);

            CoreException thrown = assertThrows(CoreException.class, () -> amount.use(-1L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "0 이하의 포인트는 사용할 수 없습니다."));
        }

        @DisplayName("소유 포인트보다 사용 포인트가 더 크면, CONFLICT 예외를 발생시킨다.")
        @Test
        void throwConflictException_whenUseOverPoint() {
            Amount amount = new Amount(1000L);

            CoreException thrown = assertThrows(CoreException.class, () -> amount.use(2000L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "포인트가 부족합니다."));
        }
    }

    @Nested
    @DisplayName("포인트가 주어진 금액보다 크거나 같은지 비교할 때,")
    class IsGreaterThanOrEqual {

        @DisplayName("포인트가 주어진 금액보다 크면, true를 반환한다.")
        @Test
        void isGreaterThanAmount() {
            Amount amount = new Amount(1000L);

            boolean result = amount.isGreaterThanOrEqual(500L);

            assertThat(result).isTrue();
        }

        @DisplayName("포인트가 주어진 금액과 같다면, true를 반환한다.")
        @Test
        void isEqualToAmount() {
            Amount amount = new Amount(1000L);

            boolean result = amount.isGreaterThanOrEqual(1000L);

            assertThat(result).isTrue();
        }

        @DisplayName("포인트가 주어진 금액보다 작으면, false를 반환한다.")
        @Test
        void isLessThanAmount() {
            Amount amount = new Amount(1000L);

            boolean result = amount.isGreaterThanOrEqual(1500L);

            assertThat(result).isFalse();
        }
    }
}
