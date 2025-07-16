package com.loopers.application.user;

import com.loopers.domain.user.UserCommand;

public class UserCriteria {
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

    public record Charge(String loginId, Long point) {
        public UserCommand.Charge toCommand() {
            return new UserCommand.Charge(
                    loginId,
                    point
            );
        }
    }
}
