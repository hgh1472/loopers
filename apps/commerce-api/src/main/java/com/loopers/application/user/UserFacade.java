package com.loopers.application.user;

import com.loopers.domain.user.LoginId;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserFacade {
    private final UserService userService;

    public UserInfo joinUser(UserCriteria.Join joinCriteria) {
        User user = userService.join(joinCriteria.toCommand());
        return UserInfo.from(user);
    }

    public UserInfo getUserInfo(String loginId) {
        User user = userService.getUser(new LoginId(loginId));
        if (user == null) {
            throw new CoreException(ErrorType.NOT_FOUND, String.format("%s 사용자를 찾을 수 없습니다.", loginId));
        }
        return UserInfo.from(user);
    }

    public PointInfo getPoints(String loginId) {
        Long points = userService.getPoints(new LoginId(loginId));
        return new PointInfo(loginId, points);
    }

    public PointInfo chargePoint(UserCriteria.Charge criteria) {
        Long totalPoint = userService.chargePoint(criteria.toCommand());
        return new PointInfo(criteria.loginId(), totalPoint);
    }
}
