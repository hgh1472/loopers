package com.loopers.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
            CouponCommand.Use command = new CouponCommand.Use(1L, 1L);
            given(couponRepository.findUserCoupon(command.couponId(), command.userId()))
                    .willReturn(Optional.empty());

            CoreException exception = assertThrows(CoreException.class, () -> couponService.use(command));
            assertThat(exception)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "쿠폰을 소유하고 있지 않습니다."));
        }
    }
}
