package com.loopers.application.order;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderLine;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.stock.Stock;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockRepository;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OrderFacadeIntegrationTest {

    @Autowired
    private OrderFacade orderFacade;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

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
                    () -> orderFacade.order(new OrderCriteria.Order(-1L, List.of(new OrderCriteria.Line(1L, 3L), new OrderCriteria.Line(1L, 2L)), delivery)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        }

        @DisplayName("주문 수량이 0 이하인 경우, BAD_REQUEST 예외를 발생시킨다.")
        @ValueSource(longs = {0, -1})
        @ParameterizedTest
        void throwBadRequestException_whenOrderQuantityIsNegative(Long quantity) {
            User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Point point = Point.from(user.getId());
            point.charge(10000L);
            pointRepository.save(point);
            Product product = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product1", new BigDecimal("1000.00"), "ON_SALE")));
            stockRepository.save(Stock.create(new StockCommand.Create(product.getId(), 100L)));
            List<OrderCriteria.Line> lines = List.of(new OrderCriteria.Line(product.getId(), quantity));
            OrderCriteria.Delivery delivery = new OrderCriteria.Delivery(
                    "황건하",
                    "010-1234-5678",
                    "서울특별시 강남구 테헤란로 123",
                    "1층 101호",
                    "요구사항"
            );

            CoreException thrown = assertThrows(CoreException.class, () -> orderFacade.order(new OrderCriteria.Order(user.getId(), lines, delivery)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "수량은 1 이상이어야 합니다."));
        }

        @DisplayName("소유 표인트가 부족한 경우, CONFLICT 예외를 발생시킨다.")
        @Test
        void throwConflictException_whenPointIsInsufficient() {
            User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Point point = Point.from(user.getId());
            pointRepository.save(point);
            Product product = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product1", new BigDecimal("1000.00"), "ON_SALE")));
            stockRepository.save(Stock.create(new StockCommand.Create(product.getId(), 100L)));
            List<OrderCriteria.Line> lines = List.of(new OrderCriteria.Line(product.getId(), 1L));
            OrderCriteria.Delivery delivery = new OrderCriteria.Delivery(
                    "황건하",
                    "010-1234-5678",
                    "서울특별시 강남구 테헤란로 123",
                    "1층 101호",
                    "요구사항"
            );

            CoreException thrown = assertThrows(CoreException.class, () -> orderFacade.order(new OrderCriteria.Order(user.getId(), lines, delivery)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "포인트가 부족합니다."));
        }

        @DisplayName("재고가 부족한 경우, CONFLICT 예외를 발생시킨다.")
        @Test
        void throwConflictException_whenStockIsInsufficient() {
            User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Point point = Point.from(user.getId());
            point.charge(10000L);
            pointRepository.save(point);
            Product product = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product1", new BigDecimal("1000.00"), "ON_SALE")));
            stockRepository.save(Stock.create(new StockCommand.Create(product.getId(), 1L)));
            List<OrderCriteria.Line> lines = List.of(new OrderCriteria.Line(product.getId(), 2L));
            OrderCriteria.Delivery delivery = new OrderCriteria.Delivery(
                    "황건하",
                    "010-1234-5678",
                    "서울특별시 강남구 테헤란로 123",
                    "1층 101호",
                    "요구사항"
            );

            CoreException thrown = assertThrows(CoreException.class, () -> orderFacade.order(new OrderCriteria.Order(user.getId(), lines, delivery)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "재고가 부족합니다."));
        }

        @DisplayName("같은 아이템이 구분되어 요청되는 경우, 하나의 요청으로 처리한다.")
        @Test
        void orderDuplicateLine() {
            User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Point point = Point.from(user.getId());
            point.charge(30000L);
            pointRepository.save(point);
            Product product = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product1", new BigDecimal("5000.00"), "ON_SALE")));
            stockRepository.save(Stock.create(new StockCommand.Create(product.getId(), 100L)));
            OrderCriteria.Delivery delivery = new OrderCriteria.Delivery(
                    "황건하",
                    "010-1234-5678",
                    "서울특별시 강남구 테헤란로 123",
                    "1층 101호",
                    "요구사항"
            );
            OrderResult orderResult = orderFacade.order(new OrderCriteria.Order(user.getId(), List.of(new OrderCriteria.Line(product.getId(), 3L), new OrderCriteria.Line(product.getId(), 2L)), delivery));

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
            Product product1 = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product1", new BigDecimal("1000.00"), "ON_SALE")));
            stockRepository.save(Stock.create(new StockCommand.Create(product1.getId(), 100L)));
            Product product2 = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product2", new BigDecimal("2000.00"), "ON_SALE")));
            stockRepository.save(Stock.create(new StockCommand.Create(product2.getId(), 100L)));
            OrderCriteria.Delivery delivery = new OrderCriteria.Delivery(
                    "황건하",
                    "010-1234-5678",
                    "서울특별시 강남구 테헤란로 123",
                    "1층 101호",
                    "요구사항"
            );

            OrderResult orderResult = orderFacade.order(new OrderCriteria.Order(user.getId(), List.of(new OrderCriteria.Line(product1.getId(), 3L), new OrderCriteria.Line(product2.getId(), 2L)), delivery));

            Optional<Order> order = orderRepository.findById(orderResult.id());
            Point afterPoint = pointRepository.findByUserId(user.getId()).get();
            assertAll(
                    () -> assertThat(order).isPresent(),
                    () -> assertThat(afterPoint.getAmount().getValue()).isEqualTo(3000L),
                    () -> assertThat(order.get().getOrderLines()).extracting("productId", "quantity", "amount")
                            .contains(tuple(product1.getId(), 3L, product1.getPrice().getValue().multiply(BigDecimal.valueOf(3))),
                                    tuple(product2.getId(), 2L, product2.getPrice().getValue().multiply(BigDecimal.valueOf(2)))),
                    () -> assertThat(order.get().getOrderPayment().getPaymentAmount()).isEqualTo(new BigDecimal("7000.00"))
            );
        }
    }

    @Nested
    @DisplayName("동시에 주문할 때,")
    class OrderConcurrency {
        @DisplayName("재고와 포인트 차감은 정확히 이루어져야 한다.")
        @Test
        void deductStock_concurrent() throws InterruptedException {
            User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Point point = Point.from(user.getId());
            point.charge(300000L);
            pointRepository.save(point);
            Product product1 = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product1", new BigDecimal("1000.00"), "ON_SALE")));
            stockRepository.save(Stock.create(new StockCommand.Create(product1.getId(), 100L)));
            Product product2 = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product2", new BigDecimal("2000.00"), "ON_SALE")));
            stockRepository.save(Stock.create(new StockCommand.Create(product2.getId(), 100L)));
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
                        orderFacade.order(new OrderCriteria.Order(user.getId(),
                                List.of(new OrderCriteria.Line(product1.getId(), 10L), new OrderCriteria.Line(product2.getId(), 10L)), delivery));
                    } catch (Exception e) {
                        System.out.println("Order failed: " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();

            Stock stock1 = stockRepository.findByProductId(product1.getId()).orElseThrow();
            assertThat(stock1.getQuantity().getValue()).isEqualTo(0);
            Stock stock2 = stockRepository.findByProductId(product2.getId()).orElseThrow();
            assertThat(stock2.getQuantity().getValue()).isEqualTo(0);
            Point usedPoint = pointRepository.findByUserId(user.getId()).orElseThrow();
            assertThat(usedPoint.getAmount().getValue()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("단일 주문 조회 시,")
    class Get {
        @DisplayName("유저 ID가 유효하지 않은 경우, NOT_FOUND 예외를 발생시킨다.")
        @Test
        void throwNotFoundException_whenUserDoesNotExist() {
            CoreException thrown = assertThrows(CoreException.class,
                    () -> orderFacade.get(new OrderCriteria.Get(-1L, 1L)));

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
            Order order = Order.of(user1.getId(), delivery);
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
