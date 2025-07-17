package com.loopers.application.user;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointService;
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
    private final PointService pointService;

    public UserInfo joinUser(UserCriteria.Join joinCriteria) {
        User user = userService.join(joinCriteria.toCommand());
        Point point = pointService.initialize(user.getId());
        return UserInfo.of(user, point);
    }

    public UserInfo getUserInfo(String loginId) {
        User user = userService.getUser(new LoginId(loginId));
        if (user == null) {
            throw new CoreException(ErrorType.NOT_FOUND, String.format("%s 사용자를 찾을 수 없습니다.", loginId));
        }
        Point point = pointService.getPoint(user.getId());
        return UserInfo.of(user, point);
    }
}
