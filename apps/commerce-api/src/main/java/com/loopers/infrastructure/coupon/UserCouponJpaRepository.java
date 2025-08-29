package com.loopers.infrastructure.coupon;

import com.loopers.domain.coupon.UserCoupon;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.couponId = :couponId AND uc.userId = :userId")
    Optional<UserCoupon> findByCouponIdAndUserIdWithLock(Long couponId, Long userId);

    Optional<UserCoupon> findByCouponIdAndUserId(Long couponId, Long userId);
}
