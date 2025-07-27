package com.loopers.application.point;

import com.loopers.domain.point.PointInfo;

public record PointResult(Long id, Long point) {
    public static PointResult from(PointInfo pointInfo) {
        return new PointResult(
                pointInfo.id(),
                pointInfo.value()
        );
    }
}
