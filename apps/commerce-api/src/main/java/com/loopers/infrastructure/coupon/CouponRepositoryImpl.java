package com.loopers.infrastructure.coupon;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.coupon.UserCoupon;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;
    private final UserCouponJpaRepository userCouponJpaRepository;

    @Override
    public Coupon save(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public UserCoupon save(UserCoupon userCoupon) {
        return userCouponJpaRepository.save(userCoupon);
    }

    @Override
    public Optional<Coupon> findById(Long couponId) {
        return couponJpaRepository.findById(couponId);
    }

    @Override
    public Optional<Coupon> findCouponWithLock(Long couponId) {
        return couponJpaRepository.findByIdWithLock(couponId);
    }

    @Override
    public Optional<UserCoupon> findUserCouponWithLock(Long couponId, Long userId) {
        return userCouponJpaRepository.findByCouponIdAndUserIdWithLock(couponId, userId);
    }

    @Override
    public Optional<UserCoupon> findUserCoupon(Long couponId, Long userId) {
        return userCouponJpaRepository.findByCouponIdAndUserId(couponId, userId);
    }
}
