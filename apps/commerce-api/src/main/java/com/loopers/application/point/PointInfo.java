package com.loopers.application.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.user.User;

public record PointInfo(String loginId, Long point) {
    public static PointInfo of(User user, Point point) {
        return new PointInfo(
                user.getLoginId().getId(),
                point.getValue()
        );
    }
}
