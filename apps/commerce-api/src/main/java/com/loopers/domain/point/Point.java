package com.loopers.domain.point;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "point")
public class Point extends BaseEntity {
    @Column(name = "value", nullable = false)
    private Long value;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    protected Point() {
    }

    private Point(Long value, Long userId) {
        this.value = value;
        this.userId = userId;
    }

    public static Point from(Long userId) {
        return new Point(0L, userId);
    }

    public void charge(Long point) {
        if (point <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "0 이하의 정수로 포인트를 충전할 수 없습니다.");
        }
        this.value += point;
    }
}
