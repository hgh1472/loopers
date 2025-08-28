package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    private PointService pointService;
    @Mock
    private PointRepository pointRepository;
    @Mock
    private PointEventPublisher pointEventPublisher;

    @Nested
    @DisplayName("포인트를 생성할 때,")
    class Initialize {
        @DisplayName("이미 유저의 포인트가 존재할 경우, CONFLICT 예외를 발생시킨다.")
        @Test
        void throwsConflictException_whenPointAlreadyExists() {
            long userId = 1L;
            given(pointRepository.existsByUserId(userId))
                    .willReturn(true);

            CoreException thrown = assertThrows(CoreException.class, () -> pointService.initialize(new PointCommand.Initialize(userId)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "이미 회원의 포인트가 존재합니다."));
        }
    }

    @Nested
    @DisplayName("포인트를 조회할 때,")
    class Find {
        @DisplayName("존재하지 않는 유저의 포인트를 조회하면, null을 반환한다.")
        @Test
        void returnNull_whenPointDoesNotExist() {
            long nonExistUserId = 1L;
            given(pointRepository.findByUserId(nonExistUserId))
                    .willReturn(Optional.empty());

            PointInfo pointInfo = pointService.findPoint(new PointCommand.Find(nonExistUserId));

            assertThat(pointInfo).isNull();
        }
    }

    @Nested
    @DisplayName("포인트를 충전할 때,")
    class Charge {
        @DisplayName("존재하지 않는 사용자의 포인트를 충전할 경우, NOT_FOUND 예외를 반환한다.")
        @Test
        void throwsNotFoundException_whenPointDoesNotExist() {

            long nonExistUserId = 1L;
            given(pointRepository.findByUserIdWithLock(nonExistUserId))
                    .willReturn(Optional.empty());

            assertThatThrownBy(() -> pointService.charge(new PointCommand.Charge(nonExistUserId, 1000L)))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("존재하지 않는 사용자입니다.");
        }

        @DisplayName("충전된 포인트를 반환한다.")
        @Test
        void returnPoint_afterCharged() {
            long userId = 1L;
            Point point = Point.from(userId);
            long initPoint = 500L;
            point.charge(initPoint);
            given(pointRepository.findByUserIdWithLock(userId))
                    .willReturn(Optional.of(point));
            long chargePoint = 1000L;

            PointInfo pointInfo = pointService.charge(new PointCommand.Charge(userId, chargePoint));

            assertThat(pointInfo.amount()).isEqualTo(initPoint + chargePoint);
        }

        @DisplayName("포인트 사용 내역을 저장한다.")
        @Test
        void recordChargeHistory() {
            long userId = 1L;
            long chargePoint = 1000L;
            Point point = Point.from(userId);

            given(pointRepository.findByUserIdWithLock(userId))
                    .willReturn(Optional.of(point));

            pointService.charge(new PointCommand.Charge(userId, chargePoint));

            verify(pointRepository, times(1))
                    .record(argThat(history ->
                            history.getPointId().equals(point.getId())
                                    && history.getAmount().equals(chargePoint)
                                    && history.getType().equals(PointHistory.Type.CHARGED))
                    );
        }

        @Test
        @DisplayName("포인트 충전 이벤트를 발행한다.")
        void publishEvent_afterCharged() {
            long userId = 1L;
            long chargePoint = 1000L;
            Point point = Point.from(userId);

            given(pointRepository.findByUserIdWithLock(userId))
                    .willReturn(Optional.of(point));

            pointService.charge(new PointCommand.Charge(userId, chargePoint));

            verify(pointEventPublisher, times(1))
                    .publish(new PointEvent.Charged(userId, chargePoint));
        }
    }

    @Nested
    @DisplayName("포인트를 사용할 때,")
    class Use {
        @DisplayName("존재하지 않는 사용자의 포인트를 사용할 경우, NOT_FOUND 예외를 반환한다.")
        @Test
        void throwsNotFoundException_whenPointDoesNotExist() {
            long nonExistUserId = 1L;
            given(pointRepository.findByUserIdWithLock(nonExistUserId))
                    .willReturn(Optional.empty());

            CoreException thrown = assertThrows(CoreException.class, () -> pointService.use(new PointCommand.Use(nonExistUserId, 1000L)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 사용자입니다."));
        }

        @DisplayName("사용 후 남은 포인트를 반환한다.")
        @Test
        void returnPoint_afterUsed() throws InsufficientPointException {
            long userId = 1L;
            long initialPoint = 1000L;
            long usePoint = 500L;
            Point point = Point.from(userId);
            point.charge(initialPoint);

            given(pointRepository.findByUserIdWithLock(userId))
                    .willReturn(Optional.of(point));

            PointInfo pointInfo = pointService.use(new PointCommand.Use(userId, usePoint));

            assertThat(pointInfo.amount()).isEqualTo(initialPoint - usePoint);
        }

        @DisplayName("포인트 사용 내역을 저장한다.")
        @Test
        void recordUseHistory() throws InsufficientPointException {
            long userId = 1L;
            long initialPoint = 1000L;
            long usePoint = 300L;
            Point point = Point.from(userId);
            point.charge(initialPoint);

            given(pointRepository.findByUserIdWithLock(userId))
                    .willReturn(Optional.of(point));

            pointService.use(new PointCommand.Use(userId, usePoint));

            verify(pointRepository, times(1))
                    .record(argThat(history ->
                            history.getPointId().equals(point.getId())
                                    && history.getAmount().equals(usePoint)
                                    && history.getType().equals(PointHistory.Type.USED))
                    );
        }

        @Test
        @DisplayName("포인트 사용 이벤트를 발행한다.")
        void publishEvent_afterUsed() throws InsufficientPointException {
            long userId = 1L;
            long initialPoint = 1000L;
            long usePoint = 300L;
            Point point = Point.from(userId);
            point.charge(initialPoint);

            given(pointRepository.findByUserIdWithLock(userId))
                    .willReturn(Optional.of(point));

            pointService.use(new PointCommand.Use(userId, usePoint));

            verify(pointEventPublisher, times(1))
                    .publish(new PointEvent.Used(userId, usePoint));
        }
    }
}
