package com.loopers.application.user;

import com.loopers.domain.user.LoginId;

public record JoinRequest(String loginId, String email, String birthDate, String gender) {
    public LoginId toLoginId() {
        return new LoginId(loginId);
    }
}
