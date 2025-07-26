package com.loopers.application.point;

import com.loopers.domain.point.PointInfo;
import com.loopers.domain.user.UserInfo;

public record PointResult(String loginId, Long point) {
    public static PointResult of(UserInfo userInfo, PointInfo pointInfo) {
        return new PointResult(
                userInfo.loginId(),
                pointInfo.value()
        );
    }
}
