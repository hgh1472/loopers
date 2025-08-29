package com.loopers.domain.coupon;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import lombok.Getter;

@Entity
@Getter
@Table(
        name = "user_coupon",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ref_user_id", "ref_coupon_id"}),
        indexes = @Index(name = "idx_user_coupon", columnList = "ref_user_id, ref_coupon_id")
)
public class UserCoupon extends BaseEntity {

    @Column(name = "ref_user_id", nullable = false)
    private Long userId;

    @Column(name = "ref_coupon_id", nullable = false)
    private Long couponId;

    @Embedded
    private DiscountPolicy discountPolicy;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Version
    private Long version;

    protected UserCoupon() {
    }

    public UserCoupon(Long userId, Long couponId, DiscountPolicy discountPolicy, LocalDateTime expiredAt) {
        this.userId = userId;
        this.couponId = couponId;
        this.discountPolicy = discountPolicy;
        this.expiredAt = expiredAt;
    }

    public static UserCoupon of(Long userId, Long couponId, DiscountPolicy discountPolicy, LocalDateTime expiredAt) {
        if (userId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID가 존재하지 않습니다.");
        }
        if (couponId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "쿠폰 ID가 존재하지 않습니다.");
        }
        if (discountPolicy == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "할인 정책이 존재하지 않습니다.");
        }
        if (expiredAt == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "쿠폰 만료 날짜가 존재하지 않습니다.");
        }

        return new UserCoupon(userId, couponId, discountPolicy, expiredAt);
    }

    public void use(LocalDateTime usedAt) {
        if (isUsed()) {
            throw new CoreException(ErrorType.CONFLICT, "이미 사용한 쿠폰입니다.");
        }
        if (isExpired(usedAt)) {
            throw new CoreException(ErrorType.CONFLICT, "이미 만료된 쿠폰입니다.");
        }
        this.usedAt = usedAt;
    }

    public void restore() {
        if (!isUsed()) {
            throw new CoreException(ErrorType.CONFLICT, "사용하지 않은 쿠폰은 복원할 수 없습니다.");
        }
        this.usedAt = null;
    }

    public boolean isUsed() {
        return this.usedAt != null;
    }

    public boolean isExpired(LocalDateTime now) {
        return this.expiredAt.isBefore(now);
    }
}
