package com.loopers.application.payment;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.coupon.DiscountPolicy;
import com.loopers.domain.coupon.UserCoupon;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentRepository;
import com.loopers.domain.payment.Refund;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.stock.Stock;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockRepository;
import com.loopers.utils.DatabaseCleanUp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PaymentFacadeIntegrationTest {

    @Autowired
    private PaymentFacade paymentFacade;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("결제 완료 처리 시,")
    class Success {

        @Test
        @DisplayName("재고가 부족한 경우, 쿠폰을 사용했다면 쿠폰은 복구된다.")
        void restoreCoupon_whenResourceProcessorFails() {
            OrderCommand.Delivery delivery = new OrderCommand.Delivery(
                    "hwang", "010-1234-5678", "서울시 강남구 역삼동 123-45", "12345", "택배");
            Order order = orderRepository.save(Order.of(new OrderCommand.Order(1L, 1L,
                    List.of(new OrderCommand.Line(1L, 1L, new BigDecimal("1000"))),
                    delivery, new BigDecimal("1000"), new BigDecimal("100"), 100L)));
            Stock stock = Stock.create(new StockCommand.Create(1L, 0L));
            stockRepository.save(stock);
            UserCoupon userCoupon = UserCoupon.of(1L, 1L, new DiscountPolicy(new BigDecimal("100"), DiscountPolicy.Type.FIXED), LocalDateTime.now().plusHours(1));
            userCoupon.use(LocalDateTime.now());
            couponRepository.save(userCoupon);
            Payment payment = Payment.of(new PaymentCommand.Pay(new BigDecimal("800"), order.getId(), "SAMSUNG", "1234-1234-1234-1234"));
            Payment savedPayment = paymentRepository.save(payment);

            paymentFacade.success(new PaymentCriteria.Success(payment.getTransactionKey(), order.getId()));

            UserCoupon after = couponRepository.findUserCoupon(1L, 1L).orElseThrow();
            assertThat(after.isUsed()).isFalse();
        }

        @Test
        @DisplayName("재고가 부족한 경우, 환불 정보가 생성된다.")
        void createRefund_whenResourceProcessorFails() {
            OrderCommand.Delivery delivery = new OrderCommand.Delivery(
                    "hwang", "010-1234-5678", "서울시 강남구 역삼동 123-45", "12345", "택배");
            Order order = orderRepository.save(Order.of(new OrderCommand.Order(1L, null,
                    List.of(new OrderCommand.Line(1L, 1L, new BigDecimal("1000"))),
                    delivery, new BigDecimal("1000"), new BigDecimal("100"), 100L)));
            Stock stock = Stock.create(new StockCommand.Create(1L, 0L));
            stockRepository.save(stock);
            Payment payment = Payment.of(new PaymentCommand.Pay(new BigDecimal("800"), order.getId(), "SAMSUNG", "1234-1234-1234-1234"));
            Payment savedPayment = paymentRepository.save(payment);

            paymentFacade.success(new PaymentCriteria.Success(payment.getTransactionKey(), order.getId()));

            Refund refund = paymentRepository.findRefundByPaymentId(savedPayment.getId()).orElseThrow();
            assertThat(refund.getAmount()).isEqualTo(new BigDecimal("800.00"));
        }

        @Test
        @DisplayName("재고가 부족한 경우, 주문 상태는 OUT_OF_STOCK이 된다.")
        void orderStatusOutOfStock_whenInsufficientStock() {
            OrderCommand.Delivery delivery = new OrderCommand.Delivery(
                    "hwang", "010-1234-5678", "서울시 강남구 역삼동 123-45", "12345", "택배");
            Order order = orderRepository.save(Order.of(new OrderCommand.Order(1L, null,
                    List.of(new OrderCommand.Line(1L, 1L, new BigDecimal("1000"))),
                    delivery, new BigDecimal("1000"), new BigDecimal("100"), 100L)));
            Stock stock = Stock.create(new StockCommand.Create(1L, 0L));
            stockRepository.save(stock);
            Payment payment = Payment.of(new PaymentCommand.Pay(new BigDecimal("800"), order.getId(), "SAMSUNG", "1234-1234-1234-1234"));
            Payment savedPayment = paymentRepository.save(payment);

            paymentFacade.success(new PaymentCriteria.Success(payment.getTransactionKey(), order.getId()));

            Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
            assertThat(updatedOrder.getStatus()).isEqualTo(Order.OrderStatus.OUT_OF_STOCK);
        }

        @Test
        @DisplayName("포인트가 부족한 경우, 쿠폰을 사용했다면 쿠폰은 복구된다.")
        void restoreCoupon_whenPointInsufficient() {
            OrderCommand.Delivery delivery = new OrderCommand.Delivery(
                    "hwang", "010-1234-5678", "서울시 강남구 역삼동 123-45", "12345", "택배");
            Order order = orderRepository.save(Order.of(new OrderCommand.Order(1L, 1L,
                    List.of(new OrderCommand.Line(1L, 1L, new BigDecimal("1000"))),
                    delivery, new BigDecimal("1000"), new BigDecimal("100"), 100L)));
            Stock stock = Stock.create(new StockCommand.Create(1L, 10L));
            stockRepository.save(stock);
            UserCoupon userCoupon = UserCoupon.of(1L, 1L, new DiscountPolicy(new BigDecimal("100"), DiscountPolicy.Type.FIXED), LocalDateTime.now().plusHours(1));
            userCoupon.use(LocalDateTime.now());
            couponRepository.save(userCoupon);
            Point point = Point.from(1L);
            point.charge(50L);
            pointRepository.save(point);
            Payment payment = Payment.of(new PaymentCommand.Pay(new BigDecimal("800"), order.getId(), "SAMSUNG", "1234-1234-1234-1234"));
            Payment savedPayment = paymentRepository.save(payment);

            paymentFacade.success(new PaymentCriteria.Success(payment.getTransactionKey(), order.getId()));

            UserCoupon after = couponRepository.findUserCoupon(1L, 1L).orElseThrow();
            assertThat(after.isUsed()).isFalse();
        }

        @Test
        @DisplayName("포인트가 부족한 경우, 환불 정보가 생성된다.")
        void createRefund_whenPointInsufficient() {
            OrderCommand.Delivery delivery = new OrderCommand.Delivery(
                    "hwang", "010-1234-5678", "서울시 강남구 역삼동 123-45", "12345", "택배");
            Order order = orderRepository.save(Order.of(new OrderCommand.Order(1L, null,
                    List.of(new OrderCommand.Line(1L, 1L, new BigDecimal("1000"))),
                    delivery, new BigDecimal("1000"), new BigDecimal("100"), 100L)));
            Stock stock = Stock.create(new StockCommand.Create(1L, 10L));
            stockRepository.save(stock);
            Point point = Point.from(1L);
            point.charge(50L);
            pointRepository.save(point);
            Payment payment = Payment.of(new PaymentCommand.Pay(new BigDecimal("800"), order.getId(), "SAMSUNG", "1234-1234-1234-1234"));
            Payment savedPayment = paymentRepository.save(payment);

            paymentFacade.success(new PaymentCriteria.Success(payment.getTransactionKey(), order.getId()));

            Refund refund = paymentRepository.findRefundByPaymentId(savedPayment.getId()).orElseThrow();
            assertThat(refund.getAmount()).isEqualTo(new BigDecimal("800.00"));
        }

        @Test
        @DisplayName("포인트가 부족한 경우, 주문 상태는 POINT_EXHAUSTED가 된다.")
        void orderStatusPointExhausted_whenPointInsufficient() {
            OrderCommand.Delivery delivery = new OrderCommand.Delivery(
                    "hwang", "010-1234-5678", "서울시 강남구 역삼동 123-45", "12345", "택배");
            Order order = orderRepository.save(Order.of(new OrderCommand.Order(1L, null,
                    List.of(new OrderCommand.Line(1L, 1L, new BigDecimal("1000"))),
                    delivery, new BigDecimal("1000"), new BigDecimal("100"), 100L)));
            Stock stock = Stock.create(new StockCommand.Create(1L, 10L));
            stockRepository.save(stock);
            Point point = Point.from(1L);
            point.charge(50L);
            pointRepository.save(point);
            Payment payment = Payment.of(new PaymentCommand.Pay(new BigDecimal("800"), order.getId(), "SAMSUNG", "1234-1234-1234-1234"));
            Payment savedPayment = paymentRepository.save(payment);

            paymentFacade.success(new PaymentCriteria.Success(payment.getTransactionKey(), order.getId()));

            Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
            assertThat(updatedOrder.getStatus()).isEqualTo(Order.OrderStatus.POINT_EXHAUSTED);
        }

        @Test
        @DisplayName("성공 처리 후, 주문 상태는 PAID가 된다.")
        void orderStatusPaid_whenResourcesDeductedSuccessfully() {
            OrderCommand.Delivery delivery = new OrderCommand.Delivery(
                    "hwang", "010-1234-5678", "서울시 강남구 역삼동 123-45", "12345", "택배");
            Order order = orderRepository.save(Order.of(new OrderCommand.Order(1L, null,
                    List.of(new OrderCommand.Line(1L, 1L, new BigDecimal("1000"))),
                    delivery, new BigDecimal("1000"), new BigDecimal("100"), 100L)));
            Stock stock = Stock.create(new StockCommand.Create(1L, 10L));
            stockRepository.save(stock);
            Point point = Point.from(1L);
            point.charge(10000L);
            pointRepository.save(point);
            Payment payment = Payment.of(new PaymentCommand.Pay(new BigDecimal("800"), order.getId(), "SAMSUNG", "1234-1234-1234-1234"));
            Payment savedPayment = paymentRepository.save(payment);

            paymentFacade.success(new PaymentCriteria.Success(payment.getTransactionKey(), order.getId()));

            Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
            assertThat(updatedOrder.getStatus()).isEqualTo(Order.OrderStatus.PAID);
        }

        @Test
        @DisplayName("성공 처리 후, 결제 정보는 성공 상태로 업데이트된다.")
        void paymentStatusSuccess_whenResourcesDeductedSuccessfully() {
            OrderCommand.Delivery delivery = new OrderCommand.Delivery(
                    "hwang", "010-1234-5678", "서울시 강남구 역삼동 123-45", "12345", "택배");
            Order order = orderRepository.save(Order.of(new OrderCommand.Order(1L, null,
                    List.of(new OrderCommand.Line(1L, 1L, new BigDecimal("1000"))),
                    delivery, new BigDecimal("1000"), new BigDecimal("100"), 100L)));
            Stock stock = Stock.create(new StockCommand.Create(1L, 10L));
            stockRepository.save(stock);
            Point point = Point.from(1L);
            point.charge(10000L);
            pointRepository.save(point);
            Payment payment = Payment.of(new PaymentCommand.Pay(new BigDecimal("800"), order.getId(), "SAMSUNG", "1234-1234-1234-1234"));
            Payment savedPayment = paymentRepository.save(payment);

            paymentFacade.success(new PaymentCriteria.Success(payment.getTransactionKey(), order.getId()));

            Payment updatedPayment = paymentRepository.findById(savedPayment.getId()).orElseThrow();
            assertThat(updatedPayment.getStatus()).isEqualTo(Payment.Status.COMPLETED);
        }
    }

    @Nested
    @DisplayName("결제 실패 처리 시,")
    class Fail {

        @Test
        @DisplayName("쿠폰을 사용했다면 쿠폰은 복구된다.")
        void restoreCoupon_whenPaymentFails() {
            OrderCommand.Delivery delivery = new OrderCommand.Delivery(
                    "hwang", "010-1234-5678", "서울시 강남구 역삼동 123-45", "12345", "택배");
            Order order = orderRepository.save(Order.of(new OrderCommand.Order(1L, 1L,
                    List.of(new OrderCommand.Line(1L, 1L, new BigDecimal("1000"))),
                    delivery, new BigDecimal("1000"), new BigDecimal("100"), 100L)));
            UserCoupon userCoupon = UserCoupon.of(1L, 1L, new DiscountPolicy(new BigDecimal("100"), DiscountPolicy.Type.FIXED), LocalDateTime.now().plusHours(1));
            userCoupon.use(LocalDateTime.now());
            couponRepository.save(userCoupon);
            Payment payment = Payment.of(new PaymentCommand.Pay(new BigDecimal("800"), order.getId(), "SAMSUNG", "1234-1234-1234-1234"));
            Payment savedPayment = paymentRepository.save(payment);

            paymentFacade.fail(new PaymentCriteria.Fail(savedPayment.getTransactionKey(), order.getId(), "Payment failed"));

            UserCoupon after = couponRepository.findUserCoupon(1L, 1L).orElseThrow();
            assertThat(after.isUsed()).isFalse();
        }

        @Test
        @DisplayName("주문과 결제 정보는 실패로 기록된다.")
        void orderAndPaymentStatusFail_whenPaymentFails() {
            OrderCommand.Delivery delivery = new OrderCommand.Delivery(
                    "hwang", "010-1234-5678", "서울시 강남구 역삼동 123-45", "12345", "택배");
            Order order = orderRepository.save(Order.of(new OrderCommand.Order(1L, null,
                    List.of(new OrderCommand.Line(1L, 1L, new BigDecimal("1000"))),
                    delivery, new BigDecimal("1000"), new BigDecimal("100"), 100L)));
            Payment payment = Payment.of(new PaymentCommand.Pay(new BigDecimal("800"), order.getId(), "SAMSUNG", "1234-1234-1234-1234"));
            Payment savedPayment = paymentRepository.save(payment);
            PaymentCriteria.Fail criteria = new PaymentCriteria.Fail(savedPayment.getTransactionKey(), order.getId(), "Payment failed");
            paymentFacade.fail(criteria);

            Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
            Payment updatedPayment = paymentRepository.findById(savedPayment.getId()).orElseThrow();
            assertThat(updatedOrder.getStatus()).isEqualTo(Order.OrderStatus.PAYMENT_FAILED);
            assertThat(updatedPayment.getStatus()).isEqualTo(Payment.Status.FAILED);
            assertThat(updatedPayment.getReason()).isEqualTo(criteria.reason());
        }
    }
}
