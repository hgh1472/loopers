package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Embeddable
public class DiscountPolicy {

    @Column(name = "value", nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    @Enumerated(EnumType.STRING)
    private Type type;

    protected DiscountPolicy() {
    }

    public DiscountPolicy(BigDecimal value, Type type) {
        if (value == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "할인 금액 또는 할인율은 필수입니다.");
        }
        if (type == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "쿠폰 유형은 필수입니다.");
        }
        if (type == Type.RATE && (value.compareTo(BigDecimal.ZERO) <= 0 || value.compareTo(BigDecimal.ONE) > 0)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "할인율은 0보다 크고 1 이하이어야 합니다.");
        }
        if (type == Type.FIXED && value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "고정 할인 금액 은 0보다 커야 합니다.");
        }

        this.value = value;
        this.type = type;
    }

    public enum Type {
        RATE,
        FIXED
    }
}
