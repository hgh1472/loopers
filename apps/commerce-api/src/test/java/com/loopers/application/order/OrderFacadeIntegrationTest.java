package com.loopers.application.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.coupon.DiscountPolicy;
import com.loopers.domain.coupon.DiscountPolicy.Type;
import com.loopers.domain.coupon.UserCoupon;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderLine;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.payment.GatewayResponse;
import com.loopers.domain.payment.PaymentGateway;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class OrderFacadeIntegrationTest {

    @Autowired
    private OrderFacade orderFacade;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @MockitoBean
    private PaymentGateway paymentGateway;

    @BeforeEach
    void setUp() {
        given(paymentGateway.request(any()))
                .willReturn(new GatewayResponse.Request(true, "TX-KEY"));
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("주문 시,")
    class OrderTests {

        @DisplayName("사용자가 존재하지 않는 경우, NOT_FOUND 예외를 발생시킨다.")
        @Test
        void throwNotFoundException_whenUserDoesNotExist() {
            OrderCriteria.Delivery delivery = new OrderCriteria.Delivery(
                    "황건하",
                    "010-1234-5678",
                    "서울특별시 강남구 테헤란로 123",
                    "1층 101호",
                    "요구사항"
            );
            CoreException thrown = assertThrows(CoreException.class,
                    () -> orderFacade.order(
                            new OrderCriteria.Order(-1L, List.of(
                                    new OrderCriteria.Line(1L, 3L), new OrderCriteria.Line(1L, 2L)), delivery, null, 100L, "SAMSUNG", "0000-0000-0000-0000")));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        }

        @DisplayName("주문 수량이 음수인 경우, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenOrderQuantityIsNegative() {
            User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Point point = Point.from(user.getId());
            point.charge(10000L);
            pointRepository.save(point);
            Product product = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product1", new BigDecimal("1000.00"), "ON_SALE")));
            List<OrderCriteria.Line> lines = List.of(new OrderCriteria.Line(product.getId(), -1L));
            OrderCriteria.Delivery delivery = new OrderCriteria.Delivery(
                    "황건하",
                    "010-1234-5678",
                    "서울특별시 강남구 테헤란로 123",
                    "1층 101호",
                    "요구사항"
            );

            CoreException thrown = assertThrows(CoreException.class, () -> orderFacade.order(new OrderCriteria.Order(user.getId(), lines, delivery, null, 100L, "SAMSUNG", "0000-0000-0000-0000")));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "총 금액은 0 이상이어야 합니다."));
        }

        @DisplayName("주문 수량이 0인 경우, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenOrderQuantityIsZero() {
            User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Point point = Point.from(user.getId());
            point.charge(10000L);
            pointRepository.save(point);
            Product product = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product1", new BigDecimal("1000.00"), "ON_SALE")));
            List<OrderCriteria.Line> lines = List.of(new OrderCriteria.Line(product.getId(), 0L));
            OrderCriteria.Delivery delivery = new OrderCriteria.Delivery(
                    "황건하",
                    "010-1234-5678",
                    "서울특별시 강남구 테헤란로 123",
                    "1층 101호",
                    "요구사항"
            );

            CoreException thrown = assertThrows(CoreException.class, () -> orderFacade.order(new OrderCriteria.Order(user.getId(), lines, delivery, null, 100L, "SAMSUNG", "0000-0000-0000-0000")));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "수량은 1 이상이어야 합니다."));
        }

        @DisplayName("같은 아이템이 구분되어 요청되는 경우, 하나의 요청으로 처리한다.")
        @Test
        void orderDuplicateLine() {
            User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Point point = Point.from(user.getId());
            point.charge(10000L);
            pointRepository.save(point);
            Product product = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product1", new BigDecimal("5000"), "ON_SALE")));
            OrderCriteria.Delivery delivery = new OrderCriteria.Delivery(
                    "황건하",
                    "010-1234-5678",
                    "서울특별시 강남구 테헤란로 123",
                    "1층 101호",
                    "요구사항"
            );
            List<OrderCriteria.Line> lines = List.of(new OrderCriteria.Line(product.getId(), 3L), new OrderCriteria.Line(product.getId(), 2L));
            OrderResult orderResult = orderFacade.order(new OrderCriteria.Order(user.getId(), lines, delivery, null, 0L, "SAMSUNG", "0000-0000-0000-0000"));

            assertAll(
                    () -> assertThat(orderResult.lines().size()).isEqualTo(1),
                    () -> assertThat(orderResult.lines().get(0).productId()).isEqualTo(product.getId()),
                    () -> assertThat(orderResult.lines().get(0).quantity()).isEqualTo(5L),
                    () -> assertThat(orderResult.payment().paymentAmount()).isEqualTo(product.getPrice().getValue().multiply(BigDecimal.valueOf(5)))
            );
        }

        @DisplayName("정상적으로 요청하는 경우, 주문이 성공적으로 처리되어야 한다.")
        @Test
        void orderSuccess() {
            User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Point point = Point.from(user.getId());
            point.charge(10000L);
            pointRepository.save(point);
            couponRepository.save(UserCoupon.of(user.getId(), 1L, new DiscountPolicy(BigDecimal.valueOf(1000), Type.FIXED), LocalDateTime.now().plusDays(10)));
            Product product1 = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product1", new BigDecimal("1000.00"), "ON_SALE")));
            Product product2 = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product2", new BigDecimal("2000.00"), "ON_SALE")));
            OrderCriteria.Delivery delivery = new OrderCriteria.Delivery(
                    "황건하",
                    "010-1234-5678",
                    "서울특별시 강남구 테헤란로 123",
                    "1층 101호",
                    "요구사항"
            );
            List<OrderCriteria.Line> lines = List.of(new OrderCriteria.Line(product1.getId(), 3L), new OrderCriteria.Line(product2.getId(), 2L));

            OrderResult orderResult = orderFacade.order(new OrderCriteria.Order(user.getId(), lines, delivery, 1L, 1000L, "SAMSUNG", "0000-0000-0000-0000"));

            Optional<Order> order = orderRepository.findById(orderResult.id());
            assertAll(
                    () -> assertThat(order).isPresent(),
                    () -> assertThat(order.get().getCouponId()).isEqualTo(1L),
                    () -> assertThat(order.get().getOrderLines()).extracting("productId", "quantity", "amount")
                            .contains(tuple(product1.getId(), 3L, product1.getPrice().getValue().multiply(BigDecimal.valueOf(3))),
                                    tuple(product2.getId(), 2L, product2.getPrice().getValue().multiply(BigDecimal.valueOf(2)))),
                    () -> assertThat(order.get().getOrderPayment().getPaymentAmount()).isEqualTo(new BigDecimal("5000.00"))
            );
        }

        @DisplayName("쿠폰을 사용하여 주문하는 경우, 쿠폰이 적용되어야 한다.")
        @Test
        void order_withCoupon() {
            User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Point point = Point.from(user.getId());
            point.charge(10000L);
            pointRepository.save(point);
            Long couponId = 1L;
            couponRepository.save(UserCoupon.of(user.getId(), couponId, new DiscountPolicy(BigDecimal.valueOf(1000), Type.FIXED), LocalDateTime.now().plusDays(10)));
            Product product1 = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product1", new BigDecimal("1000.00"), "ON_SALE")));
            Product product2 = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product2", new BigDecimal("2000.00"), "ON_SALE")));
            OrderCriteria.Delivery delivery = new OrderCriteria.Delivery(
                    "황건하",
                    "010-1234-5678",
                    "서울특별시 강남구 테헤란로 123",
                    "1층 101호",
                    "요구사항"
            );
            List<OrderCriteria.Line> lines = List.of(new OrderCriteria.Line(product1.getId(), 3L), new OrderCriteria.Line(product2.getId(), 2L));

            OrderResult orderResult = orderFacade.order(new OrderCriteria.Order(user.getId(), lines, delivery, couponId, 0L, "SAMSUNG", "0000-0000-0000-0000"));

            Order order = orderRepository.findById(orderResult.id()).orElseThrow();
            assertAll(
                    () -> assertThat(order.getCouponId()).isEqualTo(couponId),
                    () -> assertThat(order.getOrderLines()).extracting("productId", "quantity", "amount")
                            .contains(tuple(product1.getId(), 3L, product1.getPrice().getValue().multiply(BigDecimal.valueOf(3))),
                                    tuple(product2.getId(), 2L, product2.getPrice().getValue().multiply(BigDecimal.valueOf(2)))),
                    () -> assertThat(orderResult.payment().originalAmount()).isEqualTo(new BigDecimal("7000")),
                    () -> assertThat(orderResult.payment().paymentAmount()).isEqualTo(new BigDecimal("6000"))
            );
        }
    }

    @Nested
    @DisplayName("동시에 주문할 때,")
    class OrderConcurrency {

        @DisplayName("쿠폰은 단 한 번만 사용되어야 한다.")
        @Test
        void useCoupon_concurrent() throws InterruptedException {
            User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Point point = Point.from(user.getId());
            point.charge(10000L);
            pointRepository.save(point);
            Product product1 = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product1", new BigDecimal("1000.00"), "ON_SALE")));
            Product product2 = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product2", new BigDecimal("2000.00"), "ON_SALE")));
            UserCoupon coupon = couponRepository.save(UserCoupon.of(user.getId(), 1L, new DiscountPolicy(BigDecimal.valueOf(1000), Type.FIXED), LocalDateTime.now().plusDays(10)));
            List<OrderCriteria.Line> lines = List.of(new OrderCriteria.Line(product1.getId(), 10L), new OrderCriteria.Line(product2.getId(), 10L));
            OrderCriteria.Delivery delivery = new OrderCriteria.Delivery(
                    "황건하",
                    "010-1234-5678",
                    "서울특별시 강남구 테헤란로 123",
                    "1층 101호",
                    "요구사항"
            );
            int threadCount = 10;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    try {
                        orderFacade.order(new OrderCriteria.Order(user.getId(), lines, delivery, coupon.getCouponId(), 100L, "SAMSUNG", "0000-0000-0000-0000"));
                    } catch (Exception e) {
                        System.out.println("Order failed: " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();

            List<Order> orders = orderRepository.findAllByUserId(user.getId());
            assertThat(orders).hasSize(1);
        }
    }

    @Nested
    @DisplayName("단일 주문 조회 시,")
    class Get {
        @DisplayName("유저 ID가 유효하지 않은 경우, NOT_FOUND 예외를 발생시킨다.")
        @Test
        void throwNotFoundException_whenUserDoesNotExist() {
            CoreException thrown = assertThrows(CoreException.class,
                    () -> orderFacade.get(new OrderCriteria.Get(-1L, UUID.randomUUID())));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        }

        @DisplayName("유저의 주문이 아닌 경우, CONFLICT 예외를 발생시킨다.")
        @Test
        void throwConflictException_whenOrderDoesNotBelongToUser() {
            User user1 = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            User user2 = userRepository.save(User.create(new UserCommand.Join("test2", "zofldi500@loopers.im", "1999-06-23", "MALE")));
            OrderCommand.Delivery delivery = new OrderCommand.Delivery(
                    "황건하",
                    "010-1234-5678",
                    "서울특별시 강남구 테헤란로 123",
                    "1층 101호",
                    "요구사항");
            List<OrderCommand.Line> orderLines = List.of(new OrderCommand.Line(1L, 2L, new BigDecimal("1000.00")));
            OrderCommand.Order cmd = new OrderCommand.Order(user1.getId(), null, orderLines, delivery, BigDecimal.valueOf(2000), BigDecimal.valueOf(2000), 0L);
            Order order = Order.of(cmd);

            order.addLine(OrderLine.from(new OrderCommand.Line(1L, 2L, new BigDecimal("1000.00"))));
            Order saved = orderRepository.save(order);

            CoreException thrown = assertThrows(CoreException.class,
                    () -> orderFacade.get(new OrderCriteria.Get(user2.getId(), saved.getId())));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "주문 정보에 접근할 수 없습니다."));
        }

        @Nested
        @DisplayName("주문 목록 조회 시,")
        class GetOrders {
            @DisplayName("사용자가 존재하지 않는 경우, NOT_FOUND 예외를 발생시킨다.")
            @Test
            void throwNotFoundException_whenUserDoesNotExist() {
                CoreException thrown = assertThrows(CoreException.class,
                        () -> orderFacade.getOrdersOf(new OrderCriteria.GetOrders(-1L)));

                assertThat(thrown)
                        .usingRecursiveComparison()
                        .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다."));
            }
        }
    }
}
