package com.loopers.application.user;

import com.loopers.domain.point.PointInfo;
import com.loopers.domain.point.PointService;
import com.loopers.domain.user.UserInfo;
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

    public UserResult joinUser(UserCriteria.Join joinCriteria) {
        UserInfo userInfo = userService.join(joinCriteria.toCommand());
        PointInfo pointInfo = pointService.initialize(userInfo.id());
        return UserResult.of(userInfo, pointInfo);
    }

    public UserResult getUser(Long userId) {
        UserInfo userInfo = userService.findUser(userId);
        if (userInfo == null) {
            throw new CoreException(ErrorType.NOT_FOUND, String.format("%s 사용자를 찾을 수 없습니다.", userId));
        }
        PointInfo pointInfo = pointService.getPoint(userInfo.id());
        return UserResult.of(userInfo, pointInfo);
    }
}
