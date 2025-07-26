package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;

@Getter
@Embeddable
public class Email {
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$";

    private String address;

    protected Email() {
    }

    public Email(String address) {
        if (address == null || !address.matches(EMAIL_PATTERN)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이메일은 xx@yy.zz 형식이어야 합니다.");
        }
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Email email = (Email) o;
        return Objects.equals(address, email.address);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(address);
    }
}
