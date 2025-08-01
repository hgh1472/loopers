package com.loopers.application.user;

import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointCommand.Initialize;
import com.loopers.domain.point.PointInfo;
import com.loopers.domain.point.PointService;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class UserFacade {
    private final UserService userService;
    private final PointService pointService;

    @Transactional
    public UserResult joinUser(UserCriteria.Join joinCriteria) {
        UserInfo userInfo = userService.join(joinCriteria.toCommand());
        PointInfo pointInfo = pointService.initialize(new Initialize(userInfo.id()));
        return UserResult.of(userInfo, pointInfo);
    }

    public UserResult getUser(UserCriteria.Get criteria) {
        UserInfo userInfo = userService.findUser(new UserCommand.Find(criteria.userId()));
        if (userInfo == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        PointInfo pointInfo = pointService.findPoint(new PointCommand.Find(userInfo.id()));
        return UserResult.of(userInfo, pointInfo);
    }
}
