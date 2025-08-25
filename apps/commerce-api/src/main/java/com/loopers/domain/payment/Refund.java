package com.loopers.domain.payment;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;

@Entity
@Getter
@Table(name = "refund")
public class Refund extends BaseEntity {

    @Column(name = "transaction_key")
    private String transactionKey;

    @Column(name = "ref_payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "ref_order_id", nullable = false)
    private UUID orderId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    protected Refund() {
    }

    protected Refund(String transactionKey, Long paymentId, UUID orderId, BigDecimal amount) {
        this.transactionKey = transactionKey;
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
    }

    public static Refund from(Payment payment) {
        if (payment == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "결제 정보가 필요합니다.");
        }
        return new Refund(
                payment.getTransactionKey(),
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount()
        );
    }
}
