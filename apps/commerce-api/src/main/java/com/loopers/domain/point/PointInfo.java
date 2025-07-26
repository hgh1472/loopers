package com.loopers.domain.point;

public record PointInfo(
        Long id,
        Long value,
        Long userId
) {
    public static PointInfo from(Point point) {
        return new PointInfo(
                point.getId(),
                point.getValue(),
                point.getUserId()
        );
    }
}
