package com.loopers.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.domain.coupon.DiscountPolicy.Type;
import com.loopers.utils.DatabaseCleanUp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CouponServiceIntegrationTest {
    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("쿠폰 사용 시,")
    class Use {
        @DisplayName("쿠폰을 중복해서 사용할 경우, 하나의 요청만 성공한다.")
        @Test
        void useCoupon_concurrency() throws InterruptedException {
            DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000.00"), Type.FIXED);
            UserCoupon userCoupon = couponRepository.save(UserCoupon.of(1L, 1L, discountPolicy, LocalDateTime.now().plusHours(24)));
            CouponCommand.Use cmd = new CouponCommand.Use(1L, 1L);
            int threadCount = 10;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            int failCount = 0;
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    try {
                        couponService.use(cmd);
                    } catch (Exception e) {
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();

            UserCoupon afterUse = couponRepository.findUserCoupon(1L, 1L).orElseThrow();
            assertThat(afterUse.getVersion()).isEqualTo(1);
        }
    }

    @DisplayName("사용한 쿠폰 정보를 반환한다.")
    @Test
    void returnUserCouponInfo() {
        DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000.00"), Type.FIXED);
        UserCoupon userCoupon = couponRepository.save(UserCoupon.of(1L, 1L, discountPolicy, LocalDateTime.now().plusHours(24)));
        CouponCommand.Use cmd = new CouponCommand.Use(1L, 1L);

        UserCouponInfo userCouponInfo = couponService.use(cmd);

        assertThat(userCouponInfo.id()).isEqualTo(userCoupon.getId());
        assertThat(userCouponInfo.couponId()).isEqualTo(userCoupon.getCouponId());
        assertThat(userCouponInfo.userId()).isEqualTo(userCoupon.getUserId());
        assertThat(userCouponInfo.discountPolicy()).isEqualTo(discountPolicy);
        assertThat(userCouponInfo.expiredAt()).isEqualTo(userCoupon.getExpiredAt());
    }
}
