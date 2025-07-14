package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.util.Objects;
import lombok.Getter;

@Getter
@Embeddable
public class BirthDate {
    public static final String DATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$";

    private LocalDate date;

    protected BirthDate() {
    }

    public BirthDate(String date) {
        if (date == null || !date.matches(DATE_PATTERN)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "생년월일은 yyyy-MM-dd 형식이어야 합니다.");
        }

        this.date = LocalDate.parse(date);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BirthDate birthDate = (BirthDate) o;
        return Objects.equals(date, birthDate.date);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(date);
    }
}
