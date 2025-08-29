package com.loopers.domain.coupon;

import java.util.Optional;

public interface CouponRepository {

    Coupon save(Coupon coupon);

    UserCoupon save(UserCoupon userCoupon);

    Optional<Coupon> findById(Long couponId);

    Optional<Coupon> findCouponWithLock(Long couponId);

    Optional<UserCoupon> findUserCouponWithLock(Long couponId, Long userId);

    Optional<UserCoupon> findUserCoupon(Long couponId, Long userId);
}
