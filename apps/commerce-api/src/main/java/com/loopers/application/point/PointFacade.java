package com.loopers.application.point;

import com.loopers.domain.point.PointInfo;
import com.loopers.domain.point.PointService;
import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PointFacade {
    private final PointService pointService;
    private final UserService userService;

    public PointResult getPoint(String loginId) {
        UserInfo userInfo = userService.getUser(loginId);
        if (userInfo == null) {
            throw new CoreException(ErrorType.NOT_FOUND, String.format("%s 사용자를 찾을 수 없습니다.", loginId));
        }
        PointInfo pointInfo = pointService.getPoint(userInfo.id());
        return PointResult.of(userInfo, pointInfo);
    }

    @Transactional
    public PointResult charge(PointCriteria.Charge criteria) {
        UserInfo userInfo = userService.getUser(criteria.loginId());
        if (userInfo == null) {
            throw new CoreException(ErrorType.NOT_FOUND, String.format("%s 사용자를 찾을 수 없습니다.", criteria.loginId()));
        }
        PointInfo pointInfo = pointService.charge(criteria.toCommand(userInfo.id()));
        return PointResult.of(userInfo, pointInfo);
    }
}
