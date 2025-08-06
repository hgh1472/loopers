package com.loopers.domain.coupon;

import java.util.Optional;

public interface CouponRepository {

    UserCoupon save(UserCoupon userCoupon);

    Optional<UserCoupon> findUserCoupon(Long couponId, Long userId);
}
