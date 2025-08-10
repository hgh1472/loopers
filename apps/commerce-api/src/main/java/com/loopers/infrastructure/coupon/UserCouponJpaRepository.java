package com.loopers.infrastructure.coupon;

import com.loopers.domain.coupon.UserCoupon;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {

    Optional<UserCoupon> findByCouponIdAndUserId(Long couponId, Long userId);
}
