package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;

public enum Gender {
    MALE, FEMALE;

    public static Gender from(String gender) {
        for (Gender g : values()) {
            if (g.name().equals(gender)) {
                return g;
            }
        }
        throw new CoreException(ErrorType.BAD_REQUEST, "성별은 두 가지 중 선택해야 합니다.");
    }
}
