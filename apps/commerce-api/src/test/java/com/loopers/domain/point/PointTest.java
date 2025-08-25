package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "0 이하의 포인트는 충전할 수 없습니다."));
        }

        @DisplayName("포인트를 충전하면, 포인트 충전 내역이 생성된다.")
        @Test
        void returnPointHistory_whenPointCharge() {
            Point point = Point.from(1L);

            long chargeAmount = 1000L;
            PointHistory chargeHistory = point.charge(chargeAmount);

            assertAll(
                    () -> assertThat(chargeHistory.getPointId()).isEqualTo(point.getId()),
                    () -> assertThat(chargeHistory.getType()).isEqualTo(PointHistory.Type.CHARGED),
                    () -> assertThat(chargeHistory.getAmount()).isEqualTo(chargeAmount)
            );
        }

        @Nested
        @DisplayName("포인트 사용 시, ")
        class Use {
            @DisplayName("0 이하의 포인트를 사용하면, BAD_REQUEST 예외를 발생시킨다.")
            @ValueSource(longs = {-1, 0})
            @ParameterizedTest(name = "사용 포인트 = {0}")
            void throwBadRequestException_whenUseWithNonPositiveAmount() {
                Point point = Point.from(1L);

                CoreException thrown = assertThrows(CoreException.class, () -> point.use(-1L));

                assertThat(thrown)
                        .usingRecursiveComparison()
                        .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "0 이하의 포인트는 사용할 수 없습니다."));
            }

            @DisplayName("소유 포인트보다 사용 포인트가 더 크면, CONFLICT 예외를 발생시킨다.")
            @Test
            void throwConflictException_whenUseOverPoint() {
                Point point = Point.from(1L);
                point.charge(1000L);

                InsufficientPointException thrown = assertThrows(InsufficientPointException.class, () -> point.use(2000L));

                assertThat(thrown)
                        .usingRecursiveComparison()
                        .isEqualTo(new InsufficientPointException(ErrorType.CONFLICT, "포인트가 부족합니다."));
            }

            @DisplayName("소유 포인트와 사용 포인트가 같으면, 포인트 사용이 가능하다.")
            @Test
            void usePoint_whenPointEquals() throws InsufficientPointException {
                Point point = Point.from(1L);
                point.charge(1000L);

                PointHistory useHistory = point.use(1000L);

                assertAll(
                        () -> assertThat(useHistory.getPointId()).isEqualTo(point.getId()),
                        () -> assertThat(useHistory.getType()).isEqualTo(PointHistory.Type.USED),
                        () -> assertThat(useHistory.getAmount()).isEqualTo(1000L),
                        () -> assertThat(point.getAmount().getValue()).isEqualTo(0L)
                );
            }

            @DisplayName("포인트 사용 내역을 반환한다.")
            @Test
            void returnUseHistory() throws InsufficientPointException {
                Point point = Point.from(1L);
                point.charge(1000L);

                long useAmount = 500L;
                PointHistory useHistory = point.use(useAmount);

                assertAll(
                        () -> assertThat(useHistory.getPointId()).isEqualTo(point.getId()),
                        () -> assertThat(useHistory.getType()).isEqualTo(PointHistory.Type.USED),
                        () -> assertThat(useHistory.getAmount()).isEqualTo(useAmount),
                        () -> assertThat(point.getAmount().getValue()).isEqualTo(500L)
                );
            }
        }
    }

    @Nested
    @DisplayName("특정 포인트를 사용가능한지 비교할 때,")
    class CanAfford {
        @DisplayName("보유 포인트가 더 크다면, true를 반환한다.")
        @Test
        void returnTrue_whenGreater() {
            Point point = Point.from(1L);
            point.charge(1000L);

            boolean canAfford = point.canAfford(500L);

            assertThat(canAfford).isTrue();
        }

        @DisplayName("보유 포인트와 같다면, true를 반환한다.")
        @Test
        void returnTrue_whenEqual() {
            Point point = Point.from(1L);
            point.charge(1000L);

            boolean canAfford = point.canAfford(1000L);

            assertThat(canAfford).isTrue();
        }

        @DisplayName("보유 포인트가 더 작다면, false를 반환한다.")
        @Test
        void returnFalse_whenLess() {
            Point point = Point.from(1L);
            point.charge(1000L);

            boolean canAfford = point.canAfford(1500L);

            assertThat(canAfford).isFalse();
        }
    }
}
