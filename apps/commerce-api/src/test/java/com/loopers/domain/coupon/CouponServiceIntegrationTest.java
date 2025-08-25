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
            CouponCommand.Use cmd = new CouponCommand.Use(1L, 1L, new BigDecimal("5000.00"));
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

        @DisplayName("사용한 쿠폰 정보를 반환한다.")
        @Test
        void returnUserCouponInfo() {
            DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000.00"), Type.FIXED);
            UserCoupon userCoupon = couponRepository.save(UserCoupon.of(1L, 1L, discountPolicy, LocalDateTime.now().plusHours(24)));
            CouponCommand.Use cmd = new CouponCommand.Use(1L, 1L, new BigDecimal("5000.00"));

            UserCouponInfo.Use use = couponService.use(cmd);

            assertThat(use.id()).isEqualTo(userCoupon.getId());
            assertThat(use.originalAmount()).isEqualTo(cmd.originalAmount());
            assertThat(use.discountAmount()).isEqualTo(new BigDecimal("1000"));
        }
    }

    @Nested
    @DisplayName("쿠폰 발급 시,")
    class Issue {
        @DisplayName("동시에 발급 요청을 하는 경우, 쿠폰 수량은 정확히 계산된다.")
        @Test
        void calculateQuantity_Concurrency() throws InterruptedException {
            String name = "루퍼스 쿠폰";
            DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000"), DiscountPolicy.Type.FIXED);
            BigDecimal minimumOrderAmount = BigDecimal.ZERO;
            Integer expireHours = 24;
            Long initialRemainingQuantity = 10L;
            CouponCommand.Create cmd = new CouponCommand.Create(name, discountPolicy, minimumOrderAmount, expireHours, initialRemainingQuantity);
            Coupon coupon = couponRepository.save(Coupon.of(cmd));

            int threadCount = 10;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                Long userId = (long) i;
                executorService.submit(() -> {
                    try {
                        couponService.issue(new CouponCommand.Issue(coupon.getId(), userId));
                    } catch (Exception e) {
                        System.out.println("Error issuing coupon for " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();

            Coupon after = couponRepository.findById(coupon.getId()).orElseThrow();
            assertThat(after.getRemainingQuantity()).isEqualTo(0);
            assertThat(after.getIssuedQuantity()).isEqualTo(threadCount);
        }

        @DisplayName("한 유저가 동시에 발급을 요청하는 경우, 하나의 요청만 성공한다.")
        @Test
        void issueCoupon_concurrency() throws InterruptedException {
            String name = "루퍼스 쿠폰";
            DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000"), DiscountPolicy.Type.FIXED);
            BigDecimal minimumOrderAmount = BigDecimal.ZERO;
            Integer expireHours = 24;
            Long initialRemainingQuantity = 10L;
            CouponCommand.Create cmd = new CouponCommand.Create(name, discountPolicy, minimumOrderAmount, expireHours, initialRemainingQuantity);
            Coupon coupon = couponRepository.save(Coupon.of(cmd));

            int threadCount = 10;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            Long userId = 1L;
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    try {
                        couponService.issue(new CouponCommand.Issue(coupon.getId(), userId));
                    } catch (Exception e) {
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();

            Coupon after = couponRepository.findById(coupon.getId()).orElseThrow();
            assertThat(after.getIssuedQuantity()).isEqualTo(1);
            assertThat(after.getRemainingQuantity()).isEqualTo(9);
            UserCoupon userCoupon = couponRepository.findUserCoupon(coupon.getId(), userId).orElseThrow();
            assertThat(userCoupon.getCouponId()).isEqualTo(coupon.getId());
        }

        @DisplayName("발급된 쿠폰 정보를 반환한다.")
        @Test
        void returnCouponInfo() {
            String name = "루퍼스 쿠폰";
            DiscountPolicy discountPolicy = new DiscountPolicy(new BigDecimal("1000.00"), DiscountPolicy.Type.FIXED);
            BigDecimal minimumOrderAmount = new BigDecimal("100.00");
            Integer expireHours = 24;
            Long initialRemainingQuantity = 10L;
            CouponCommand.Create cmd = new CouponCommand.Create(name, discountPolicy, minimumOrderAmount, expireHours, initialRemainingQuantity);
            Coupon coupon = couponRepository.save(Coupon.of(cmd));
            Long userId = 1L;

            CouponInfo couponInfo = couponService.issue(new CouponCommand.Issue(coupon.getId(), userId));

            assertThat(couponInfo.id()).isEqualTo(coupon.getId());
            assertThat(couponInfo.name()).isEqualTo(coupon.getName());
            assertThat(couponInfo.discountPolicy()).isEqualTo(coupon.getDiscountPolicy());
            assertThat(couponInfo.minimumOrderAmount()).isEqualTo(coupon.getMinimumOrderAmount());
            assertThat(couponInfo.expireHours()).isEqualTo(coupon.getExpireHours());
            assertThat(couponInfo.remainingQuantity()).isEqualTo(9);
            assertThat(couponInfo.issuedQuantity()).isEqualTo(1);
        }
    }
}
