package com.loopers.domain.coupon;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class CouponTest {

    @Nested
    @DisplayName("쿠폰을 생성할 시,")
    class Create {
        @DisplayName("쿠폰 이름이 존재하지 않으면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwBadRequestException_whenNameIsNullOrBlank() {
            DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000"), DiscountPolicy.Type.FIXED);
            BigDecimal minimumOrderAmount = BigDecimal.ZERO;
            Integer expireHours = 24;
            Long remainingQuantity = 100L;
            CouponCommand.Create cmd = new CouponCommand.Create(null, discountPolicy, minimumOrderAmount, expireHours, remainingQuantity);

            CoreException thrown = assertThrows(CoreException.class, () -> Coupon.of(cmd));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "쿠폰 이름은 필수입니다."));
        }

        @DisplayName("할인 정책이 존재하지 않으면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwBadRequestException_whenTypeIsNull() {
            String name = "루퍼스 쿠폰";
            BigDecimal discountValue = new BigDecimal("10.00");
            BigDecimal minimumOrderAmount = BigDecimal.ZERO;
            Integer expireHours = 24;
            Long remainingQuantity = 100L;
            CouponCommand.Create cmd = new CouponCommand.Create(name, null, minimumOrderAmount, expireHours, remainingQuantity);

            CoreException thrown = assertThrows(CoreException.class, () -> Coupon.of(cmd));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "할인 정책은 필수입니다."));
        }

        @DisplayName("최소 주문 금액이 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwBadRequestException_whenMinimumOrderAmountIsNull() {
            String name = "루퍼스 쿠폰";
            DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000"), DiscountPolicy.Type.FIXED);
            Integer expireHours = 24;
            Long remainingQuantity = 100L;
            CouponCommand.Create cmd = new CouponCommand.Create(name, discountPolicy, null, expireHours, remainingQuantity);

            CoreException thrown = assertThrows(CoreException.class, () -> Coupon.of(cmd));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "최소 주문 금액은 필수입니다."));
        }

        @DisplayName("최소 주문 금액이 0보다 작으면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwBadRequestException_whenMinimumOrderAmountIsLessThanZero() {
            String name = "루퍼스 쿠폰";
            DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000"), DiscountPolicy.Type.FIXED);
            BigDecimal minimumOrderAmount = new BigDecimal("-1");
            Integer expireHours = 24;
            Long remainingQuantity = 100L;
            CouponCommand.Create cmd = new CouponCommand.Create(name, discountPolicy, minimumOrderAmount, expireHours, remainingQuantity);

            CoreException thrown = assertThrows(CoreException.class, () -> Coupon.of(cmd));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "쿠폰을 적용하기 위한 최소 주문 금액은 0 이상이어야 합니다."));
        }

        @DisplayName("유효 시간이 null이거나 1시간 미만이면, BAD_REQUEST 예외가 발생한다.")
        @NullSource
        @ValueSource(ints = {0, -1})
        @ParameterizedTest
        void throwBadRequestException_whenExpireHoursIsNullOrLessThanOne(Integer expireHours) {
            String name = "루퍼스 쿠폰";
            DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000"), DiscountPolicy.Type.FIXED);
            BigDecimal minimumOrderAmount = BigDecimal.ZERO;
            Long remainingQuantity = 100L;
            CouponCommand.Create cmd = new CouponCommand.Create(name, discountPolicy, minimumOrderAmount, expireHours, remainingQuantity);

            CoreException thrown = assertThrows(CoreException.class, () -> Coupon.of(cmd));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "쿠폰 유효 시간은 1시간 이상이어야 합니다."));
        }

        @DisplayName("쿠폰 남은 수량이 null이거나 0보다 작으면, BAD_REQUEST 예외가 발생한다.")
        @NullSource
        @ValueSource(longs = {-1})
        @ParameterizedTest
        void throwBadRequestException_whenRemainingQuantityIsNullOrLessThanZero(Long remainingQuantity) {
            String name = "루퍼스 쿠폰";
            DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000"), DiscountPolicy.Type.FIXED);
            BigDecimal minimumOrderAmount = BigDecimal.ZERO;
            Integer expireHours = 24;
            CouponCommand.Create cmd = new CouponCommand.Create(name, discountPolicy, minimumOrderAmount, expireHours, remainingQuantity);

            CoreException thrown = assertThrows(CoreException.class, () -> Coupon.of(cmd));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "쿠폰 수량은 0 이상이어야 합니다."));
        }
    }

    @Nested
    @DisplayName("쿠폰을 발급할 시,")
    class Issue {
        @DisplayName("쿠폰 수량이 0보다 작으면, CONFLICT 예외가 발생한다.")
        @Test
        void throwConflictException_whenRemainingQuantityIsZero() {
            String name = "루퍼스 쿠폰";
            DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000"), DiscountPolicy.Type.FIXED);
            BigDecimal minimumOrderAmount = BigDecimal.ZERO;
            Integer expireHours = 24;
            Coupon coupon = Coupon.of(new CouponCommand.Create(name, discountPolicy, minimumOrderAmount, expireHours, 0L));

            CoreException thrown = assertThrows(CoreException.class, () -> coupon.issue(1L, LocalDateTime.now()));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "쿠폰이 모두 소진되었습니다."));
        }

        @DisplayName("남은 쿠폰 수량이 감소하고, 발급 쿠폰 수량은 증가힌다.")
        @Test
        void decreaseRemainingQuantity_whenIssueCoupon() {
            String name = "루퍼스 쿠폰";
            DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000"), DiscountPolicy.Type.FIXED);
            BigDecimal minimumOrderAmount = BigDecimal.ZERO;
            Integer expireHours = 24;
            Long initialRemainingQuantity = 10L;
            Coupon coupon = Coupon.of(new CouponCommand.Create(name, discountPolicy, minimumOrderAmount, expireHours, initialRemainingQuantity));

            UserCoupon userCoupon = coupon.issue(1L, LocalDateTime.now());

            assertThat(userCoupon.getUserId()).isEqualTo(1L);
            assertThat(coupon.getRemainingQuantity()).isEqualTo(initialRemainingQuantity - 1);
            assertThat(coupon.getIssuedQuantity()).isEqualTo(1L);
        }

        @DisplayName("유저 쿠폰이 발급된다.")
        @Test
        void issueUserCoupon_whenIssueCoupon() {
            String name = "루퍼스 쿠폰";
            DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000"), DiscountPolicy.Type.FIXED);
            BigDecimal minimumOrderAmount = BigDecimal.ZERO;
            Integer expireHours = 24;
            Long initialRemainingQuantity = 10L;
            Coupon coupon = Coupon.of(new CouponCommand.Create(name, discountPolicy, minimumOrderAmount, expireHours, initialRemainingQuantity));
            LocalDateTime issuedAt = LocalDateTime.now();

            UserCoupon userCoupon = coupon.issue(1L, issuedAt);

            assertThat(userCoupon).isNotNull();
            assertThat(userCoupon.getUserId()).isEqualTo(1L);
            assertThat(userCoupon.getDiscountPolicy()).isEqualTo(coupon.getDiscountPolicy());
            assertThat(userCoupon.getUsedAt()).isNull();
            assertThat(userCoupon.getExpiredAt()).isEqualTo(issuedAt.plusHours(coupon.getExpireHours()));
        }
    }
}
