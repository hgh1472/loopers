package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.Getter;

@Embeddable
@Getter
public class OrderPayment {

    private BigDecimal originalAmount;


    private BigDecimal paymentAmount;

    protected OrderPayment() {
    }

    public OrderPayment(BigDecimal originalAmount, BigDecimal paymentAmount) {
        if (originalAmount == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "총 금액이 존재하지 않습니다.");
        }
        if (originalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "총 금액은 0 이상이어야 합니다.");
        }
        if (paymentAmount == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "결제 금액이 존재하지 않습니다.");
        }
        if (paymentAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "결제 금액은 0 이상이어야 합니다.");
        }
        this.originalAmount = originalAmount;
        this.paymentAmount = paymentAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderPayment that = (OrderPayment) o;
        return Objects.equals(paymentAmount, that.paymentAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(paymentAmount);
    }
}
