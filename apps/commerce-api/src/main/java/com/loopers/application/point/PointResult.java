package com.loopers.application.point;

import com.loopers.domain.point.PointInfo;

public record PointResult(Long id, Long amount) {
    public static PointResult from(PointInfo pointInfo) {
        return new PointResult(
                pointInfo.id(),
                pointInfo.amount()
        );
    }
}
