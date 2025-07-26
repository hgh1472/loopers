package com.loopers.domain.user;

import java.time.LocalDate;

public record UserInfo(
        Long id,
        String loginId,
        String email,
        LocalDate birthDate,
        String gender
) {
    public static UserInfo from(User user) {
        return new UserInfo(
                user.getId(),
                user.getLoginId().getId(),
                user.getEmail().getAddress(),
                user.getBirthDate().getDate(),
                user.getGender().name()
        );
    }
}
