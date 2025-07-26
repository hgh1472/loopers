package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PointTest {

    @Nested
    class Charge {
        @DisplayName("0 이하의 정수로 포인트를 충전 시 BAD_REQUEST 예외를 발생시킨다.")
        @ValueSource(longs = {-1, 0})
        @ParameterizedTest(name = "충전 포인트 = {0}")
        void charge_withNonPositivePoint(Long chargePoint) {
            Point point = Point.from(1L);

            CoreException thrown = assertThrows(CoreException.class, () -> point.charge(chargePoint));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "0 이하의 정수로 포인트를 충전할 수 없습니다."));
        }
    }
}
