package com.loopers.interfaces.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.coupon.DiscountPolicy;
import com.loopers.domain.coupon.DiscountPolicy.Type;
import com.loopers.domain.coupon.UserCoupon;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderCommand.Line;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.payment.GatewayResponse;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentGateway;
import com.loopers.domain.payment.PaymentRepository;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.stock.Stock;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockRepository;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserRepository;
import com.loopers.interfaces.api.payment.PaymentV1Dto;
import com.loopers.utils.DatabaseCleanUp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentApiE2ETest {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CouponRepository couponRepository;
    @MockitoBean
    private PaymentGateway paymentGateway;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("POST /api/v1/payment")
    class Pay {

        final String url = "/api/v1/payments";

        @Test
        @DisplayName("결제 요청이 오면, 결제 정보가 저장되고, 주문 상태가 변경된다.")
        void pay() {
            given(paymentGateway.request(any(), any()))
                    .willReturn(new GatewayResponse.Request(true, "TX-KEY"));
            OrderCommand.Delivery delivery = new OrderCommand.Delivery(
                    "hwang", "010-1234-5678", "서울시 강남구 역삼동 123-45", "12345", "택배");
            Order order = orderRepository.save(Order.of(new OrderCommand.Order(1L, null,
                    List.of(new OrderCommand.Line(1L, 1L, new BigDecimal("1000"))),
                    delivery, new BigDecimal("1000"), new BigDecimal("100"), 100L)));

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("X-USER-ID", "1");
            PaymentV1Dto.PaymentRequest payRequest = new PaymentV1Dto.PaymentRequest(order.getId(), "SAMSUNG", "1234-1234-1234-1234");
            ParameterizedTypeReference<ApiResponse<PaymentV1Dto.PaymentResponse>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<PaymentV1Dto.PaymentResponse>> response =
                    testRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(payRequest, httpHeaders), responseType);

            Payment payment = paymentRepository.findByTransactionKey("TX-KEY").orElseThrow();
            assertThat(response.getBody().data().transactionKey()).isEqualTo(payment.getTransactionKey());
            assertThat(response.getBody().data().amount()).isEqualTo(payment.getAmount());
            assertThat(response.getBody().data().orderId()).isEqualTo(order.getId());
            assertThat(response.getBody().data().status()).isEqualTo(Payment.Status.PENDING.toString());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/payment/callback")
    class Callback {

        @Test
        @DisplayName("성공 콜백이 올 경우, 결제 상태는 성공으로 변경된다.")
        void successCallback() {
            User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            UUID orderId = UUID.randomUUID();
            Payment payment = Payment.of(new PaymentCommand.Pay(new BigDecimal("100"), orderId, "SAMSUNG", "1234-1234-1234-1234"));
            payment.successRequest("TX-KEY");
            paymentRepository.save(payment);

            HttpHeaders httpHeaders = new HttpHeaders();
            PaymentV1Dto.CallbackRequest callbackRequest = new PaymentV1Dto.CallbackRequest(
                    "TX-KEY",
                    orderId.toString(),
                    "SAMSUNG",
                    "1234-1234-1234-1234",
                    1000L,
                    PaymentV1Dto.Status.SUCCESS,
                    null
            );
            String url = "/api/v1/payments/callback";
            ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<Object>> response = testRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(callbackRequest), responseType);

            Payment afterPayment = paymentRepository.findByTransactionKey("TX-KEY").orElseThrow();
            assertThat(afterPayment.getStatus()).isEqualTo(Payment.Status.COMPLETED);
        }

        @Test
        @DisplayName("실패 폴백이 올 경우, 주문과 결제 상태가 실패로 변경되고 쿠폰이 복원된다.")
        void fallback_whenFail() {
            User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            OrderCommand.Delivery delivery = new OrderCommand.Delivery("황건하", "010-1234-5678", "서울특별시 강남구 강남대로 지하396", "강남역 지하 XX", "요구사항");
            List<Line> lines1 = List.of(new OrderCommand.Line(1L, 2L, new BigDecimal("1000")));
            OrderCommand.Order cmd = new OrderCommand.Order(1L, 1L, lines1, delivery, new BigDecimal("8000"), new BigDecimal("8000"), 100L);
            Order order = Order.of(cmd);
            order.pending();
            Order savedOrder = orderRepository.save(order);
            stockRepository.save(Stock.create(new StockCommand.Create(1L, 0L)));
            UserCoupon userCoupon = UserCoupon.of(user.getId(), 1L, new DiscountPolicy(new BigDecimal("100"), Type.FIXED), LocalDateTime.now().plusDays(1));
            userCoupon.use(LocalDateTime.now());
            UserCoupon savedCoupon = couponRepository.save(userCoupon);
            Point point = Point.from(user.getId());
            point.charge(10000L);
            pointRepository.save(point);

            Payment payment = Payment.of(new PaymentCommand.Pay(new BigDecimal("100"), order.getId(), "SAMSUNG", "1234-1234-1234-1234"));
            payment.successRequest("TX-KEY");
            Payment savedPayment = paymentRepository.save(payment);

            HttpHeaders httpHeaders = new HttpHeaders();
            PaymentV1Dto.CallbackRequest callbackRequest = new PaymentV1Dto.CallbackRequest(
                    "TX-KEY",
                    savedOrder.getId().toString(),
                    "SAMSUNG",
                    "1234-1234-1234-1234",
                    order.getOrderPayment().getPaymentAmount().longValue(),
                    PaymentV1Dto.Status.FAILED,
                    "한도 초과"
            );
            String url = "/api/v1/payments/callback";
            ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<Object>> response = testRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(callbackRequest), responseType);

            Order afterOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();
            Payment afterPayment = paymentRepository.findById(savedPayment.getId()).orElseThrow();
            UserCoupon afterCoupon = couponRepository.findUserCoupon(1L, user.getId()).orElseThrow();
            assertThat(afterCoupon.isUsed()).isFalse();
            assertThat(afterOrder.getStatus()).isEqualTo(Order.OrderStatus.PAYMENT_FAILED);
            assertThat(afterPayment.getStatus()).isEqualTo(Payment.Status.FAILED);
        }
    }
}
