package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.loopers.support.error.CoreException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private PointRepository pointRepository;

    @Nested
    @DisplayName("포인트를 조회할 때,")
    class Get {
        @DisplayName("존재하지 않는 유저의 포인트를 조회하면, null을 반환한다.")
        @Test
        void returnNull_whenPointDoesNotExist() {
            long nonExistUserId = 1L;
            BDDMockito.given(pointRepository.findByUserId(nonExistUserId))
                    .willReturn(Optional.empty());

            PointInfo pointInfo = pointService.getPoint(nonExistUserId);

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
            BDDMockito.given(pointRepository.findByUserId(nonExistUserId))
                    .willReturn(Optional.empty());

            assertThatThrownBy(() -> pointService.charge(new PointCommand.Charge(nonExistUserId, 1000L)))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("존재하지 않는 사용자입니다.");
        }
    }
}
