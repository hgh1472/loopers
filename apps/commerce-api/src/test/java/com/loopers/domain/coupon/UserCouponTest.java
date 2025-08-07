package com.loopers.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserCouponTest {
    @Nested
    @DisplayName("유저 쿠폰 생성 시,")
    class Create {
        @DisplayName("사용자 ID가 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwBadRequestException_whenUserIdIsNull() {
            Long userId = null;
            Long couponId = 1L;
            LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);
            DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000.00"), DiscountPolicy.Type.FIXED);

            Exception exception = assertThrows(CoreException.class, () -> UserCoupon.of(userId, couponId, discountPolicy, expiredAt));

            assertThat(exception)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "사용자 ID가 존재하지 않습니다."));
        }

        @DisplayName("쿠폰 ID가 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwBadRequestException_whenCouponIdIsNull() {
            Long userId = 1L;
            Long couponId = null;
            LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);
            DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000.00"), DiscountPolicy.Type.FIXED);

            Exception exception = assertThrows(CoreException.class, () -> UserCoupon.of(userId, couponId, discountPolicy, expiredAt));

            assertThat(exception)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "쿠폰 ID가 존재하지 않습니다."));
        }

        @DisplayName("만료 날짜가 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwBadRequestException_whenExpiredAtIsNull() {
            Long userId = 1L;
            Long couponId = 1L;
            LocalDateTime expiredAt = null;
            DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000.00"), DiscountPolicy.Type.FIXED);

            Exception exception = assertThrows(CoreException.class, () -> UserCoupon.of(userId, couponId, discountPolicy, expiredAt));

            assertThat(exception)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "쿠폰 만료 날짜가 존재하지 않습니다."));
        }

        @DisplayName("할인 정책이 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwBadRequestException_whenDiscountPolicyIsNull() {
            Long userId = 1L;
            Long couponId = 1L;
            LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);

            Exception exception = assertThrows(CoreException.class, () -> UserCoupon.of(userId, couponId, null, expiredAt));

            assertThat(exception)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "할인 정책이 존재하지 않습니다."));
        }
    }

    @Nested
    @DisplayName("유저 쿠폰 사용 시,")
    class Use {
        @DisplayName("이미 사용한 쿠폰을 사용하려고 하면, CONFLICT 예외가 발생한다.")
        @Test
        void throwConflictException_whenCouponAlreadyUsed() {
            Long userId = 1L;
            Long couponId = 1L;
            LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);
            DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000.00"), DiscountPolicy.Type.FIXED);
            UserCoupon userCoupon = UserCoupon.of(userId, couponId, discountPolicy, expiredAt);
            userCoupon.use(LocalDateTime.now());

            Exception exception = assertThrows(CoreException.class, () -> userCoupon.use(LocalDateTime.now()));

            assertThat(exception)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "이미 사용한 쿠폰입니다."));
        }

        @DisplayName("만료된 쿠폰을 사용하려고 하면, CONFLICT 예외가 발생한다.")
        @Test
        void throwConflictException_whenCouponExpired() {
            Long userId = 1L;
            Long couponId = 1L;
            LocalDateTime expiredAt = LocalDateTime.now().minusDays(1);
            DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000.00"), DiscountPolicy.Type.FIXED);
            UserCoupon userCoupon = UserCoupon.of(userId, couponId, discountPolicy, expiredAt);

            Exception exception = assertThrows(CoreException.class, () -> userCoupon.use(LocalDateTime.now()));

            assertThat(exception)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "이미 만료된 쿠폰입니다."));
        }

        @DisplayName("유효한 쿠폰을 사용하면, 사용 시간이 기록된다.")
        @Test
        void recordUsedAt_whenCouponIsValid() {
            Long userId = 1L;
            Long couponId = 1L;
            LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);
            DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000.00"), DiscountPolicy.Type.FIXED);
            UserCoupon userCoupon = UserCoupon.of(userId, couponId, discountPolicy, expiredAt);

            LocalDateTime usedAt = LocalDateTime.now();
            userCoupon.use(usedAt);

            assertThat(userCoupon.getUsedAt()).isEqualTo(usedAt);
        }
    }

    @DisplayName("쿠폰이 사용되었는지 확인할 때, 이미 사용된 경우라면 false를 반환한다.")
    @Test
    void returnFalse_whenUserCouponUsed() {
        Long userId = 1L;
        Long couponId = 1L;
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(7);
        DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000.00"), DiscountPolicy.Type.FIXED);
        UserCoupon userCoupon = UserCoupon.of(userId, couponId, discountPolicy, expiredAt);
        userCoupon.use(LocalDateTime.now());

        boolean isUsed = userCoupon.isUsed();

        assertThat(isUsed).isTrue();
    }

    @DisplayName("쿠폰이 만료되었는지 확인할 때, 만료된 경우라면 true를 반환한다.")
    @Test
    void returnTrue_whenUserCouponExpired() {
        Long userId = 1L;
        Long couponId = 1L;
        LocalDateTime expiredAt = LocalDateTime.now().minusDays(1);
        DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000.00"), DiscountPolicy.Type.FIXED);
        UserCoupon userCoupon = UserCoupon.of(userId, couponId, discountPolicy, expiredAt);

        boolean isExpired = userCoupon.isExpired(LocalDateTime.now());

        assertThat(isExpired).isTrue();
    }
}
