package com.loopers.domain.user;

public class UserCommand {
    public record Join(String loginId, String email, String birthDate, String gender) {
        public LoginId toLoginId() {
            return new LoginId(loginId);
        }

        public Email toEmail() {
            return new Email(email);
        }

        public BirthDate toBirthDate() {
            return new BirthDate(birthDate);
        }

        public User.Gender toGender() {
            return User.Gender.from(gender);
        }
    }

    public record Find(Long userId) {
    }
}
