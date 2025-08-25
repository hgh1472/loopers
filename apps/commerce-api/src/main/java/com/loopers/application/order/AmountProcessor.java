package com.loopers.application.order;

import com.loopers.domain.coupon.CouponCommand;
import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.coupon.UserCouponInfo;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointInfo;
import com.loopers.domain.point.PointService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AmountProcessor {

    private final PointService pointService;
    private final CouponService couponService;

    public AmountResult applyDiscount(Long couponId, Long userId, List<OrderCommand.Line> lines, Long pointAmount) {
        BigDecimal originalAmount = calculateOriginalAmountOf(lines);
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (couponId != null) {
            UserCouponInfo.Use useInfo = couponService.use(new CouponCommand.Use(couponId, userId, originalAmount));
            discountAmount = discountAmount.add(useInfo.discountAmount());
        }

        if (pointAmount == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용할 포인트에 대한 정보가 존재하지 않습니다.");
        }
        if (pointAmount < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문에 사용할 포인트 금액은 0 이상이어야 합니다.");
        }
        PointInfo pointInfo = pointService.findPoint(new PointCommand.Find(userId));
        if (pointInfo == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "사용자의 포인트 정보가 존재하지 않습니다.");
        }
        if (pointInfo.amount() < pointAmount) {
            throw new CoreException(ErrorType.CONFLICT, "포인트가 부족합니다.");
        }
        pointAmount = Math.min(pointAmount, originalAmount.subtract(discountAmount).longValue());
        return new AmountResult(originalAmount, discountAmount, pointAmount);
    }

    private BigDecimal calculateOriginalAmountOf(List<OrderCommand.Line> lines) {
        return lines.stream()
                .map(OrderCommand.Line::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.FLOOR);
    }
}
