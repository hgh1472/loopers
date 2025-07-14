package com.loopers.application.user;

import com.loopers.domain.user.BirthDate;
import com.loopers.domain.user.Email;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.LoginId;
import com.loopers.domain.user.User;

public record UserInfo(Long id, LoginId loginId, Email email, BirthDate birthDate, Gender gender, Long point) {
    public static UserInfo from(User user) {
        return new UserInfo(
                user.getId(),
                user.getLoginId(),
                user.getEmail(),
                user.getBirthDate(),
                user.getGender(),
                user.getPoint()
        );
    }
}
