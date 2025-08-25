package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Embeddable
@Getter
@EqualsAndHashCode
public class OrderPayment {

    private BigDecimal originalAmount;

    private BigDecimal discountAmount;

    private Long pointAmount;

    private BigDecimal paymentAmount;

    protected OrderPayment() {
    }

    public OrderPayment(BigDecimal originalAmount, BigDecimal discountAmount, Long pointAmount) {
        if (originalAmount == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "총 금액이 존재하지 않습니다.");
        }
        if (originalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "총 금액은 0 이상이어야 합니다.");
        }
        if (discountAmount == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "할인 금액에 대한 정보가 존재하지 않습니다.");
        }
        if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "할인 금액은 0 이상이어야 합니다.");
        }
        if (pointAmount == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용한 포인트에 대한 정보가 존재하지 않습니다.");
        }
        if (pointAmount < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용한 포인트는 0 이상이어야 합니다.");
        }
        this.originalAmount = originalAmount.setScale(0, RoundingMode.FLOOR);
        this.discountAmount = discountAmount.setScale(0, RoundingMode.FLOOR);
        this.pointAmount = pointAmount;
        this.paymentAmount = originalAmount.subtract(discountAmount)
                .subtract(BigDecimal.valueOf(pointAmount)).setScale(0, RoundingMode.FLOOR);
    }
}
