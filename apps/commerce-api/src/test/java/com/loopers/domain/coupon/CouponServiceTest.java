package com.loopers.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;
    @Mock
    private CouponRepository couponRepository;

    @Nested
    @DisplayName("쿠폰 사용 시,")
    class Use {

        @DisplayName("쿠폰을 소유하고 있지 않은 경우, NOT_FOUND 예외가 발생한다.")
        @Test
        void throwNotFoundException_whenCouponNotOwned() {
            CouponCommand.Use command = new CouponCommand.Use(1L, 1L, new BigDecimal("10000"));
            given(couponRepository.findUserCoupon(command.couponId(), command.userId()))
                    .willReturn(Optional.empty());

            CoreException exception = assertThrows(CoreException.class, () -> couponService.use(command));
            assertThat(exception)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "쿠폰을 소유하고 있지 않습니다."));
        }
    }

    @Nested
    @DisplayName("쿠폰 발급 시,")
    class Issue {

        @DisplayName("쿠폰을 찾을 수 없는 경우, NOT_FOUND 예외가 발생한다.")
        @Test
        void throwNotFoundException_whenCouponNotFound() {
            CouponCommand.Issue command = new CouponCommand.Issue(1L, 1L);
            given(couponRepository.findCouponWithLock(command.couponId()))
                    .willReturn(Optional.empty());

            CoreException exception = assertThrows(CoreException.class, () -> couponService.issue(command));
            assertThat(exception)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "쿠폰을 찾을 수 없습니다."));
        }

        @DisplayName("쿠폰을 이미 발급받은 경우, CONFLICT 예외가 발생한다.")
        @Test
        void throwConflictException_whenCouponAlreadyOwned() {
            String name = "루퍼스 쿠폰";
            DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000"), DiscountPolicy.Type.FIXED);
            BigDecimal minimumOrderAmount = BigDecimal.ZERO;
            Integer expireHours = 24;
            Long initialRemainingQuantity = 10L;
            Coupon coupon = Coupon.of(new CouponCommand.Create(name, discountPolicy, minimumOrderAmount, expireHours, initialRemainingQuantity));
            CouponCommand.Issue command = new CouponCommand.Issue(1L, 1L);
            given(couponRepository.findCouponWithLock(1L))
                    .willReturn(Optional.of(coupon));
            given(couponRepository.save(any(UserCoupon.class)))
                    .willThrow(new DataIntegrityViolationException("이미 쿠폰 소유"));

            CoreException exception = assertThrows(CoreException.class, () -> couponService.issue(command));

            assertThat(exception)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "이미 쿠폰을 소유하고 있습니다."));
        }
    }
}
