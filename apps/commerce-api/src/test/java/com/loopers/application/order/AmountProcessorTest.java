package com.loopers.application.order;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointInfo;
import com.loopers.domain.point.PointService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AmountProcessorTest {
    @InjectMocks
    private AmountProcessor amountProcessor;
    @Mock
    private CouponService couponService;
    @Mock
    private PointService pointService;

    @Nested
    @DisplayName("주문 생성 내 가격 계산 시,")
    class AmountProcess {

        @DisplayName("쿠폰 id가 null인 경우, 할인 금액이 적용되지 않아야 한다.")
        @Test
        void doesNotApplyDiscount_whenCouponIdIsNull() {
            Long couponId = null;
            Long userId = 1L;
            List<OrderCommand.Line> lines = List.of(
                    new OrderCommand.Line(1L, 2L, BigDecimal.valueOf(1000)),
                    new OrderCommand.Line(2L, 2L, BigDecimal.valueOf(500))
            );
            given(pointService.findPoint(new PointCommand.Find(userId)))
                    .willReturn(new PointInfo(1L, 100L, userId));

            AmountResult result = amountProcessor.applyDiscount(couponId, userId, lines, 0L);

            assertThat(result.discountAmount()).isEqualTo(BigDecimal.ZERO);
        }

        @DisplayName("포인트가 null인 경우, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequest_whenPointAmountIsNull() {
            List<OrderCommand.Line> lines = List.of(
                    new OrderCommand.Line(1L, 2L, BigDecimal.valueOf(1000)),
                    new OrderCommand.Line(2L, 2L, BigDecimal.valueOf(500))
            );
            CoreException thrown = assertThrows(CoreException.class, () -> amountProcessor.applyDiscount(null, 1L, lines, null));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "사용할 포인트에 대한 정보가 존재하지 않습니다."));
        }

        @DisplayName("포인트가 음수인 경우, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwsBadRequest_whenPointAmountIsNegative() {
            List<OrderCommand.Line> lines = List.of(
                    new OrderCommand.Line(1L, 2L, BigDecimal.valueOf(1000)),
                    new OrderCommand.Line(2L, 2L, BigDecimal.valueOf(500))
            );
            CoreException thrown = assertThrows(CoreException.class, () -> amountProcessor.applyDiscount(null, 1L, lines, -100L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "주문에 사용할 포인트 금액은 0 이상이어야 합니다."));
        }

        @DisplayName("사용자의 포인트 정보가 없는 경우, NOT_FOUND 예외가 발생한다.")
        @Test
        void throwsNotFound_whenUserPointInfoDoesNotExist() {
            Long userId = 1L;
            List<OrderCommand.Line> lines = List.of(
                    new OrderCommand.Line(1L, 2L, BigDecimal.valueOf(1000)),
                    new OrderCommand.Line(2L, 2L, BigDecimal.valueOf(500))
            );
            Long pointAmount = 100L;
            CoreException thrown = assertThrows(CoreException.class, () -> amountProcessor.applyDiscount(null, userId, lines, pointAmount));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "사용자의 포인트 정보가 존재하지 않습니다."));
        }

        @DisplayName("사용자의 포인트가 부족한 경우, CONFLICT 예외가 발생한다.")
        @Test
        void throwsConflict_whenUserPointIsInsufficient() {
            Long userId = 1L;
            List<OrderCommand.Line> lines = List.of(
                    new OrderCommand.Line(1L, 2L, BigDecimal.valueOf(1000)),
                    new OrderCommand.Line(2L, 2L, BigDecimal.valueOf(500))
            );
            Long pointAmount = 100L;

            given(pointService.findPoint(new PointCommand.Find(userId))).willReturn(new PointInfo(1L, 50L, userId));

            CoreException thrown = assertThrows(CoreException.class, () -> amountProcessor.applyDiscount(null, userId, lines, pointAmount));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "포인트가 부족합니다."));
        }
    }
}
