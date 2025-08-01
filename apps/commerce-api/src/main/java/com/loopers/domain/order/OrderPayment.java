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

    private BigDecimal paymentAmount;

    protected OrderPayment() {
    }

    public OrderPayment(BigDecimal paymentAmount) {
        if (paymentAmount == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "결제 금액이 존재하지 않습니다.");
        }
        if (paymentAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "결제 금액은 0 이상이어야 합니다.");
        }

        this.paymentAmount = paymentAmount;
    }

    public OrderPayment add(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "추가 금액은 0 이상이어야 합니다.");
        }
        return new OrderPayment(this.paymentAmount.add(amount));
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
