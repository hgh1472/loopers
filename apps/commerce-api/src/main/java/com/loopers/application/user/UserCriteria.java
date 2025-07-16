package com.loopers.application.user;

import com.loopers.domain.user.UserCommand;

public record UserCriteria() {
    public record Join(String loginId, String email, String birthDate, String gender) {
        public UserCommand.Join toCommand() {
            return new UserCommand.Join(
                    loginId,
                    email,
                    birthDate,
                    gender
            );
        }
    }
}
