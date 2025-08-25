package com.loopers.domain.point;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "point")
public class Point extends BaseEntity {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "amount", nullable = false))
    private Amount amount;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    protected Point() {
    }

    private Point(Long amount, Long userId) {
        this.amount = new Amount(amount);
        this.userId = userId;
    }

    public static Point from(Long userId) {
        return new Point(0L, userId);
    }

    public PointHistory charge(Long amount) {
        this.amount.charge(amount);
        return PointHistory.chargeOf(getId(), amount);
    }

    public PointHistory use(Long amount) throws InsufficientPointException {
        this.amount.use(amount);
        return PointHistory.useOf(getId(), amount);
    }

    public boolean canAfford(Long amount) {
        return this.amount.isGreaterThanOrEqual(amount);
    }

    public Amount getAmount() {
        return new Amount(this.amount.getValue());
    }

    public Long getUserId() {
        return userId;
    }
}
