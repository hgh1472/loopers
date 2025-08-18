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

    @Column(name = "ref_order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "card_type", nullable = false)
    private CardType cardType;

    @Embedded
    private CardNo cardNo;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "reason")
    private String reason;

    protected Payment() {
    }

    protected Payment(String transactionKey, Long orderId, BigDecimal amount, CardType cardType, CardNo cardNo, Status status,
                      String reason) {
        this.transactionKey = transactionKey;
        this.orderId = orderId;
        this.amount = amount;
        this.cardType = cardType;
        this.cardNo = cardNo;
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
                CardType.from(command.cardType()),
                new CardNo(command.cardNo()),
                Status.PENDING,
                null
        );
    }

    public enum CardType {
        SAMSUNG,
        KB,
        HYUNDAI;

        public static CardType from(String type) {
            try {
                return CardType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CoreException(ErrorType.BAD_REQUEST, "지원하지 않는 카드사입니다.");
            }
        }
    }

    public enum Status {
        PENDING,
        COMPLETED,
        FAILED,
        NEED_REFUND
    }
}
