package com.loopers.domain.point;

public record PointInfo(
        Long id,
        Long amount,
        Long userId
) {
    public static PointInfo from(Point point) {
        return new PointInfo(
                point.getId(),
                point.getAmount().getValue(),
                point.getUserId()
        );
    }
}
