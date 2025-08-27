package com.loopers.application.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.coupon.DiscountPolicy;
import com.loopers.domain.coupon.UserCoupon;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.payment.GatewayResponse;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentGateway;
import com.loopers.domain.payment.PaymentRepository;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class PaymentFacadeIntegrationTest {

    @Autowired
    private PaymentFacade paymentFacade;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CouponRepository couponRepository;
    @MockitoBean
    private PaymentGateway paymentGateway;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("결제 요청 시,")
    class Pay {

        @Test
        @DisplayName("결제 정보가 저장되고, 주문 상태는 PENDING으로 변한다.")
        void paymentSaved_whenPay() {
            given(paymentGateway.request(any(), any()))
                    .willReturn(new GatewayResponse.Request(true, "TX-KEY"));
            OrderCommand.Delivery delivery = new OrderCommand.Delivery(
                    "hwang", "010-1234-5678", "서울시 강남구 역삼동 123-45", "12345", "택배");
            Order order = orderRepository.save(Order.of(new OrderCommand.Order(1L, null,
                    List.of(new OrderCommand.Line(1L, 1L, new BigDecimal("1000"))),
                    delivery, new BigDecimal("1000"), new BigDecimal("100"), 100L)));

            PaymentResult result = paymentFacade.pay(new PaymentCriteria.Pay(order.getId(), "SAMSUNG", "1234-1234-1234-1234"));

            Payment payment = paymentRepository.findByTransactionKey(result.transactionKey()).orElseThrow();
            assertThat(payment.getOrderId()).isEqualTo(result.orderId());
            assertThat(payment.getAmount()).isEqualTo(result.paymentAmount());
            assertThat(payment.getStatus()).isEqualTo(Payment.Status.PENDING);
            assertThat(payment.getReason()).isEqualTo(result.reason());
            assertThat(payment.getTransactionKey()).isEqualTo(result.transactionKey());
            Order afterOrder = orderRepository.findById(order.getId()).orElseThrow();
            assertThat(afterOrder.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
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
            Order order = Order.of(new OrderCommand.Order(1L, 1L,
                    List.of(new OrderCommand.Line(1L, 1L, new BigDecimal("1000"))),
                    delivery, new BigDecimal("1000"), new BigDecimal("100"), 100L));
            order.pending();
            Order savedOrder = orderRepository.save(order);
            UserCoupon userCoupon = UserCoupon.of(1L, 1L, new DiscountPolicy(new BigDecimal("100"), DiscountPolicy.Type.FIXED), LocalDateTime.now().plusHours(1));
            userCoupon.use(LocalDateTime.now());
            couponRepository.save(userCoupon);
            Payment payment = Payment.of(new PaymentCommand.Pay(new BigDecimal("800"), savedOrder.getId(), "SAMSUNG", "1234-1234-1234-1234"));
            payment.successRequest("TX-KEY");
            Payment savedPayment = paymentRepository.save(payment);

            paymentFacade.fail(new PaymentCriteria.Fail(savedPayment.getTransactionKey(), savedOrder.getId(), "Payment failed"));

            UserCoupon after = couponRepository.findUserCoupon(1L, 1L).orElseThrow();
            assertThat(after.isUsed()).isFalse();
        }

        @Test
        @DisplayName("주문과 결제 정보는 실패로 기록된다.")
        void orderAndPaymentStatusFail_whenPaymentFails() {
            OrderCommand.Delivery delivery = new OrderCommand.Delivery(
                    "hwang", "010-1234-5678", "서울시 강남구 역삼동 123-45", "12345", "택배");
            Order order = Order.of(new OrderCommand.Order(1L, null,
                    List.of(new OrderCommand.Line(1L, 1L, new BigDecimal("1000"))),
                    delivery, new BigDecimal("1000"), new BigDecimal("100"), 100L));
            order.pending();
            Order savedOrder = orderRepository.save(order);
            Payment payment = Payment.of(new PaymentCommand.Pay(new BigDecimal("800"), savedOrder.getId(), "SAMSUNG", "1234-1234-1234-1234"));
            payment.successRequest("TX-KEY");
            Payment savedPayment = paymentRepository.save(payment);
            PaymentCriteria.Fail criteria = new PaymentCriteria.Fail(savedPayment.getTransactionKey(), savedOrder.getId(), "Payment failed");

            paymentFacade.fail(criteria);

            Order updatedOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();
            Payment updatedPayment = paymentRepository.findById(savedPayment.getId()).orElseThrow();
            assertThat(updatedOrder.getStatus()).isEqualTo(Order.OrderStatus.PAYMENT_FAILED);
            assertThat(updatedPayment.getStatus()).isEqualTo(Payment.Status.FAILED);
            assertThat(updatedPayment.getReason()).isEqualTo(criteria.reason());
        }
    }
}
