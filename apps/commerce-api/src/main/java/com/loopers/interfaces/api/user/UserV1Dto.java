package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserCriteria;
import com.loopers.application.user.UserInfo;

public class UserV1Dto {
    public record JoinRequest(String loginId, String email, String birthDate, String gender) {
        public UserCriteria.Join toCriteria() {
            return new UserCriteria.Join(
                    loginId,
                    email,
                    birthDate,
                    gender
            );
        }
    }

    public record UserResponse(Long id, String loginId, String email, String birthDate, String gender, Long point) {
        public static UserResponse from(UserInfo info) {
            return new UserResponse(
                    info.id(),
                    info.loginId().getId(),
                    info.email().getAddress(),
                    info.birthDate().getDate().toString(),
                    info.gender().name(),
                    info.point()
            );
        }
    }
}
