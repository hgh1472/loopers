package com.loopers.interfaces.api.user;

import com.loopers.application.user.PointInfo;

public class PointV1Dto {
    public record PointResponse(Long point) {
        public static PointResponse from(PointInfo pointInfo) {
            return new PointResponse(pointInfo.point());
        }
    }
}
