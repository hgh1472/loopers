package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import com.loopers.domain.point.PointCommand.Charge;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
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
    class Find {
        @DisplayName("해당 ID의 회원이 존재할 경우, 보유 포인트가 반환된다.")
        @Test
        void getPoints() {
            Point saved = pointRepository.save(Point.from(1L));

            PointInfo pointInfo = pointService.findPoint(new PointCommand.Find(saved.getUserId()));

            assertThat(pointInfo.amount()).isEqualTo(0L);
        }

        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
        @Test
        void getPoints_withNonExistId() {
            PointInfo pointInfo = pointService.findPoint(new PointCommand.Find(-1L));

            assertThat(pointInfo).isNull();
        }
    }

    @Nested
    class ChargePoint {
        @DisplayName("존재하지 않는 유저 ID로 충전을 시도한 경우, NOT_FOUND 예외를 발생시킨다.")
        @Test
        void chargePoint() {

            CoreException thrown = assertThrows(CoreException.class, () -> pointService.charge(new Charge(1L, 1000L)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 사용자입니다."));
        }
    }
}
