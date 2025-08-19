package com.loopers.domain.payment;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;

@Entity
@Getter
@Table(name = "payment")
public class Payment extends BaseEntity {

    @Column(name = "transaction_key")
    private String transactionKey;

    @Column(name = "ref_order_id", nullable = false)
    private Long orderId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Embedded
    private Card card;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "reason")
    private String reason;

    protected Payment() {
    }

    protected Payment(String transactionKey, Long orderId, BigDecimal amount, Card card, Status status,
                      String reason) {
        this.transactionKey = transactionKey;
        this.orderId = orderId;
        this.amount = amount;
        this.card = card;
        this.status = status;
        this.reason = reason;
    }

    public static Payment of(PaymentCommand.Pay command) {
        if (command.amount() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "결제 금액은 필수입니다.");
        }
        if (command.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "결제 금액은 0보다 커야 합니다.");
        }
        if (command.orderId() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 ID는 필수입니다.");
        }
        if (command.cardType() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "카드사는 필수입니다.");
        }

        return new Payment(
                null,
                command.orderId(),
                command.amount(),
                Card.of(command.cardNo(), command.cardType()),
                Status.PENDING,
                null
        );
    }

    public Refund refund() {
        if (this.status == Status.FAILED) {
            throw new CoreException(ErrorType.BAD_REQUEST, "결제되지 않은 요청은 환불할 수 없습니다.");
        }
        return Refund.from(this);
    }

    public void success() {
        if (this.status != Status.PENDING) {
            throw new CoreException(ErrorType.BAD_REQUEST, "결제는 대기 상태에서만 성공할 수 있습니다.");
        }
        this.status = Status.COMPLETED;
    }

    public void fail(String reason) {
        if (this.status != Status.PENDING) {
            throw new CoreException(ErrorType.BAD_REQUEST, "결제는 대기 상태에서만 실패할 수 있습니다.");
        }
        this.status = Status.FAILED;
        this.reason = reason;
    }

    public enum Status {
        PENDING,
        COMPLETED,
        FAILED
    }
}
