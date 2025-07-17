package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointTest {

    @Nested
    class Charge {
        @DisplayName("0 이하의 정수로 포인트를 충전 시 실패한다.")
        @ValueSource(longs = {-1, 0})
        @ParameterizedTest(name = "충전 포인트 = {0}")
        void charge_withNonPositivePoint(Long chargePoint) {
            Point point = Point.from(1L);

            assertThatThrownBy(() -> point.charge(chargePoint))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("0 이하의 정수로 포인트를 충전할 수 없습니다.");
        }
    }
}
