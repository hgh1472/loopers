package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;

@Getter
@Embeddable
public class LoginId {
    public static final String LOGIN_ID_PATTERN = "^[a-zA-Z0-9]+$";

    private String id;

    protected LoginId() {
    }

    public LoginId(String id) {
        if (id == null || 10 < id.length()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "ID는 10자 이내이어야 합니다.");
        }
        if (!id.matches(LOGIN_ID_PATTERN)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "ID는 영문 및 숫자만 포함할 수 있습니다.");
        }

        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LoginId loginId = (LoginId) o;
        return Objects.equals(id, loginId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
