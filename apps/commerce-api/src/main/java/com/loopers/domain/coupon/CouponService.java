package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final DiscountPolicyAdapter discountPolicyAdapter;
    private final CouponRepository couponRepository;

    @Transactional
    public CouponInfo issue(CouponCommand.Issue command) {
        Coupon coupon = couponRepository.findCouponWithLock(command.couponId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "쿠폰을 찾을 수 없습니다."));
        UserCoupon userCoupon = coupon.issue(command.userId(), LocalDateTime.now());
        try {
            couponRepository.save(userCoupon);
        } catch (DataIntegrityViolationException e) {
            throw new CoreException(ErrorType.CONFLICT, "이미 쿠폰을 소유하고 있습니다.");
        }
        return CouponInfo.from(coupon);
    }

    @Transactional
    public UserCouponInfo.Use use(CouponCommand.Use command) {
        UserCoupon userCoupon = couponRepository.findUserCouponWithLock(command.couponId(), command.userId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "쿠폰을 소유하고 있지 않습니다."));
        DiscountStrategy discountStrategy = discountPolicyAdapter.from(userCoupon.getDiscountPolicy());
        BigDecimal paymentAmount = discountStrategy.discount(command.originalAmount());
        userCoupon.use(LocalDateTime.now());
        return new UserCouponInfo.Use(userCoupon.getId(), command.originalAmount(), paymentAmount);
    }

    @Transactional
    public UserCouponInfo restore(CouponCommand.Restore command) {
        UserCoupon userCoupon = couponRepository.findUserCouponWithLock(command.couponId(), command.userId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "쿠폰을 소유하고 있지 않습니다."));
        userCoupon.restore();
        return UserCouponInfo.from(userCoupon);
    }
}
