package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;

    @Transactional
    public UserCouponInfo use(CouponCommand.Use command) {
        UserCoupon userCoupon = couponRepository.findUserCoupon(command.couponId(), command.userId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "쿠폰을 소유하고 있지 않습니다."));
        userCoupon.use(LocalDateTime.now());
        return UserCouponInfo.from(userCoupon);
    }
}
