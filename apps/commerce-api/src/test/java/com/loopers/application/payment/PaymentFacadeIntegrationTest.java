package com.loopers.application.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.payment.GatewayResponse;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentGateway;
import com.loopers.domain.payment.PaymentRepository;
import com.loopers.utils.DatabaseCleanUp;
import java.math.BigDecimal;
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
}
