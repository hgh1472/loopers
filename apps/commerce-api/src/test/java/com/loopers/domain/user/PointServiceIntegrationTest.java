package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.point.PointService;
import com.loopers.support.error.CoreException;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
public class PointServiceIntegrationTest {
    @Autowired
    private PointService pointService;

    @MockitoSpyBean
    private PointRepository pointRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    class GetPoints {
        @DisplayName("해당 ID의 회원이 존재할 경우, 보유 포인트가 반환된다.")
        @Test
        void getPoints() {
            Point saved = pointRepository.save(Point.from(1L));

            Point point = pointService.getPoint(saved.getUserId());

            assertThat(point.getValue()).isEqualTo(0L);
        }

        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
        @Test
        void getPoints_withNonExistId() {
            Point point = pointService.getPoint(1L);

            assertThat(point).isNull();
        }
    }

    @Nested
    class ChargePoint {
        @DisplayName("존재하지 않는 유저 ID로 충전을 시도한 경우, 실패한다.")
        @Test
        void chargePoint() {
            assertThatThrownBy(() -> pointService.charge(new PointCommand.Charge(1L, 1000L)))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("존재하지 않는 사용자입니다.");
        }
    }
}
