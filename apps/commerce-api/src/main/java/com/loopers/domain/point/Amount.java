package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;

@Embeddable
@Getter
public class Amount {

    private Long value;

    protected Amount() {
    }

    public Amount(Long value) {
        if (value == null || value < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트는 0 이상이어야 합니다.");
        }
        this.value = value;
    }

    public void charge(Long amount) {
        if (amount == null || amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "0 이하의 포인트는 충전할 수 없습니다.");
        }
        this.value += amount;
    }

    public void use(Long amount) {
        if (amount == null || amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "0 이하의 포인트는 사용할 수 없습니다.");
        }
        if (this.value < amount) {
            throw new InsufficientPointException(ErrorType.CONFLICT, "포인트가 부족합니다.");
        }
        this.value -= amount;
    }

    public boolean isGreaterThanOrEqual(Long amount) {
        return this.value >= amount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Amount amount = (Amount) o;
        return Objects.equals(value, amount.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
