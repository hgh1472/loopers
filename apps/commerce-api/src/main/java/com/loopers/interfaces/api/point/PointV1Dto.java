package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointInfo;
import jakarta.validation.constraints.NotNull;

public class PointV1Dto {
    public record PointResponse(
            @NotNull
            Long point
    ) {
        public static PointResponse from(PointInfo pointInfo) {
            return new PointResponse(pointInfo.point());
        }
    }

    public record ChargeRequest(Long point) {
    }
}
