package com.loopers.application.user;

import com.loopers.domain.point.PointInfo;
import com.loopers.domain.user.UserInfo;
import java.time.LocalDate;

public record UserResult(Long id, String loginId, String email, LocalDate birthDate, String gender, Long point) {
    public static UserResult of(UserInfo userInfo, PointInfo pointInfo) {
        return new UserResult(
                userInfo.id(),
                userInfo.loginId(),
                userInfo.email(),
                userInfo.birthDate(),
                userInfo.gender(),
                pointInfo.value()
        );
    }
}
