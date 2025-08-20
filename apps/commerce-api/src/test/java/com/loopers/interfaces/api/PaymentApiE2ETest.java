package com.loopers.interfaces.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.coupon.DiscountPolicy;
import com.loopers.domain.coupon.DiscountPolicy.Type;
import com.loopers.domain.coupon.UserCoupon;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderCommand.Line;
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
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserRepository;
import com.loopers.interfaces.api.payment.PaymentV1Dto;
import com.loopers.utils.DatabaseCleanUp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("POST /api/v1/payment/callback")
    class Callback {

        @Test
        @DisplayName("성공 콜백이 올 경우, 재고와 포인트 차감이 이루어지고 주문과 결제 상태가 변경된다.")
        void successCallback() {
            User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            OrderCommand.Delivery delivery = new OrderCommand.Delivery("황건하", "010-1234-5678", "서울특별시 강남구 강남대로 지하396", "강남역 지하 XX", "요구사항");
            List<Line> lines1 = List.of(new OrderCommand.Line(1L, 2L, new BigDecimal("1000")));
            OrderCommand.Order cmd = new OrderCommand.Order(1L, null, lines1, delivery, new BigDecimal("8000"), new BigDecimal("8000"), 100L);
            Order order = Order.of(cmd);
            Order savedOrder = orderRepository.save(order);
            stockRepository.save(Stock.create(new StockCommand.Create(1L, 100L)));
            Point point = Point.from(user.getId());
            point.charge(10000L);
            pointRepository.save(point);
            Payment payment = Payment.of(new PaymentCommand.Pay(new BigDecimal("100"), order.getId(), "SAMSUNG", "1234-1234-1234-1234"));
            payment.successRequest("TX-KEY");
            paymentRepository.save(payment);

            HttpHeaders httpHeaders = new HttpHeaders();
            PaymentV1Dto.CallbackRequest callbackRequest = new PaymentV1Dto.CallbackRequest(
                    "TX-KEY",
                    savedOrder.getId().toString(),
                    "SAMSUNG",
                    "1234-1234-1234-1234",
                    order.getOrderPayment().getPaymentAmount().longValue(),
                    PaymentV1Dto.Status.SUCCESS,
                    null
            );
            String url = "/api/v1/payments/callback";
            ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<Object>> response = testRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(callbackRequest), responseType);

            Order findOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();
            Stock afterStock = stockRepository.findByProductId(1L).orElseThrow();
            Point afterPoint = pointRepository.findByUserId(user.getId()).orElseThrow();
            Payment afterPayment = paymentRepository.findByTransactionKey("TX-KEY").orElseThrow();
            assertThat(findOrder.getStatus()).isEqualTo(Order.OrderStatus.PAID);
            assertThat(afterStock.getQuantity().getValue()).isEqualTo(98L);
            assertThat(afterPoint.getAmount().getValue()).isEqualTo(9900L);
            assertThat(afterPayment.getStatus()).isEqualTo(Payment.Status.COMPLETED);
        }

        @Test
        @DisplayName("성공 콜백 중 재고 차감이 실패할 경우, 환불이 추가된다.")
        void refund_whenCallbackFails() {
            User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            OrderCommand.Delivery delivery = new OrderCommand.Delivery("황건하", "010-1234-5678", "서울특별시 강남구 강남대로 지하396", "강남역 지하 XX", "요구사항");
            List<Line> lines1 = List.of(new OrderCommand.Line(1L, 2L, new BigDecimal("1000")));
            OrderCommand.Order cmd = new OrderCommand.Order(1L, null, lines1, delivery, new BigDecimal("8000"), new BigDecimal("8000"), 100L);
            Order order = Order.of(cmd);
            Order savedOrder = orderRepository.save(order);
            stockRepository.save(Stock.create(new StockCommand.Create(1L, 0L)));
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
                    PaymentV1Dto.Status.SUCCESS,
                    null
            );
            String url = "/api/v1/payments/callback";
            ParameterizedTypeReference<ApiResponse<Object>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<Object>> response = testRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(callbackRequest), responseType);

            Optional<Refund> findRefund = paymentRepository.findRefundByPaymentId(savedPayment.getId());
            assertThat(findRefund).isPresent();
        }

        @Test
        @DisplayName("실패 폴백이 올 경우, 주문과 결제 상태가 실패로 변경되고 쿠폰이 복원된다.")
        void fallback_whenFail() {
            User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            OrderCommand.Delivery delivery = new OrderCommand.Delivery("황건하", "010-1234-5678", "서울특별시 강남구 강남대로 지하396", "강남역 지하 XX", "요구사항");
            List<Line> lines1 = List.of(new OrderCommand.Line(1L, 2L, new BigDecimal("1000")));
            OrderCommand.Order cmd = new OrderCommand.Order(1L, 1L, lines1, delivery, new BigDecimal("8000"), new BigDecimal("8000"), 100L);
            Order order = Order.of(cmd);
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
