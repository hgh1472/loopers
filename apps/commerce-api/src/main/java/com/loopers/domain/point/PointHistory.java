package com.loopers.domain.point;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "point_history")
public class PointHistory extends BaseEntity {

    @Column(name = "ref_point_id", nullable = false)
    private Long pointId;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Enumerated(value = EnumType.STRING)
    private Type type;

    protected PointHistory() {
    }

    private PointHistory(Long pointId, Long amount, Type type) {
        this.pointId = pointId;
        this.amount = amount;
        this.type = type;
    }

    public static PointHistory chargeOf(Long pointId, Long amount) {
        return new PointHistory(pointId, amount, Type.CHARGED);
    }

    public static PointHistory useOf(Long pointId, Long amount) {
        return new PointHistory(pointId, amount, Type.USED);
    }

    public enum Type {
        CHARGED,
        USED
    }
}
