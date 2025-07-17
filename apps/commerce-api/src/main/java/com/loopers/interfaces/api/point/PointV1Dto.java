package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointInfo;

public class PointV1Dto {
    public record PointResponse(Long point) {
        public static PointResponse from(PointInfo pointInfo) {
            return new PointResponse(pointInfo.point());
        }
    }

    public record ChargeRequest(Long point) {
    }
}
