package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointCriteria;
import com.loopers.application.point.PointResult;
import jakarta.validation.constraints.NotNull;

public class PointV1Dto {
    public record ChargeRequest(
            @NotNull(message = "충전할 포인트는 필수입니다.")
            Long point
    ) {
        public PointCriteria.Charge toCriteria(Long userId) {
            return new PointCriteria.Charge(userId, point);
        }
    }

    public record PointResponse(Long point) {
        public static PointResponse from(PointResult pointResult) {
            return new PointResponse(pointResult.point());
        }
    }
}
