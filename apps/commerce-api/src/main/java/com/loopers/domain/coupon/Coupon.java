package com.loopers.domain.coupon;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

@Entity
@Getter
@Table(name = "coupon")
public class Coupon extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    private DiscountPolicy discountPolicy;

    @Column(name = "minimum_order_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal minimumOrderAmount;

    @Column(name = "expire_hours", nullable = false)
    private Integer expireHours;

    @Column(name = "remaining_quantity", nullable = false)
    private Long remainingQuantity;

    @Column(name = "issued_quantity", nullable = false)
    private Long issuedQuantity;

    protected Coupon() {
    }

    private Coupon(String name, DiscountPolicy discountPolicy, BigDecimal minimumOrderAmount, Integer expireHours,
                   Long remainingQuantity, Long issuedQuantity) {
        this.name = name;
        this.discountPolicy = discountPolicy;
        this.minimumOrderAmount = minimumOrderAmount;
        this.expireHours = expireHours;
        this.remainingQuantity = remainingQuantity;
        this.issuedQuantity = issuedQuantity;
    }

    public static Coupon of(CouponCommand.Create command) {
        if (command.name() == null || command.name().isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "쿠폰 이름은 필수입니다.");
        }
        if (command.discountPolicy() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "할인 정책은 필수입니다.");
        }
        if (command.minimumOrderAmount() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "최소 주문 금액은 필수입니다.");
        }
        if (command.minimumOrderAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "쿠폰을 적용하기 위한 최소 주문 금액은 0 이상이어야 합니다.");
        }
        if (command.expireHours() == null || command.expireHours() <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "쿠폰 유효 시간은 1시간 이상이어야 합니다.");
        }
        if (command.remainingQuantity() == null || command.remainingQuantity() < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "쿠폰 수량은 0 이상이어야 합니다.");
        }

        return new Coupon(command.name(), command.discountPolicy(), command.minimumOrderAmount(), command.expireHours(),
                command.remainingQuantity(), 0L);
    }

    public UserCoupon issue(Long userId, LocalDateTime now) {
        if (remainingQuantity <= 0) {
            throw new CoreException(ErrorType.CONFLICT, "쿠폰이 모두 소진되었습니다.");
        }
        remainingQuantity--;
        issuedQuantity++;
        return UserCoupon.of(userId, this.getId(), discountPolicy, now.plusHours(expireHours));
    }
}
