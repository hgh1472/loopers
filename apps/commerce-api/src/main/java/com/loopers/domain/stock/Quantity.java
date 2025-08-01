package com.loopers.domain.stock;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Quantity {

    private Long value;

    protected Quantity() {
    }

    public Quantity(Long value) {
        if (value == null || value < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "수량은 0 이상이어야 합니다.");
        }
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity quantity = (Quantity) o;
        return value.equals(quantity.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
