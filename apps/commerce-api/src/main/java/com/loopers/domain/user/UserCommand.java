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

        public Gender toGender() {
            return Gender.from(gender);
        }
    }
}
