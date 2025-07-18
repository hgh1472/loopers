package com.loopers.application.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import com.loopers.domain.user.User;
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

    public PointInfo getPoint(String loginId) {
        User user = userService.getUser(loginId);
        if (user == null) {
            throw new CoreException(ErrorType.NOT_FOUND, String.format("%s 사용자를 찾을 수 없습니다.", loginId));
        }
        Point point = pointService.getPoint(user.getId());
        return new PointInfo(user.getLoginId().getId(), point.getValue());
    }

    @Transactional
    public PointInfo charge(PointCriteria.Charge criteria) {
        User user = userService.getUser(criteria.loginId());
        if (user == null) {
            throw new CoreException(ErrorType.NOT_FOUND, String.format("%s 사용자를 찾을 수 없습니다.", criteria.loginId()));
        }
        Point chargedPoint = pointService.charge(new PointCommand.Charge(user.getId(), criteria.point()));
        return new PointInfo(user.getLoginId().getId(), chargedPoint.getValue());
    }
}
