package com.loopers.application.order;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.domain.order.Order;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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



        @DisplayName("같은 아이템이 구분되어 요청되는 경우, 하나의 요청으로 처리한다.")
        @Test
        void orderDuplicateLine() {
            User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Point point = Point.from(user.getId());
            point.charge(10000L);
            pointRepository.save(point);
            Product product = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product1", BigDecimal.valueOf(1000L), "ON_SALE")));
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
                    () -> assertThat(orderResult.orderLineResults().size()).isEqualTo(1),
                    () -> assertThat(orderResult.orderLineResults().get(0).productId()).isEqualTo(product.getId()),
                    () -> assertThat(orderResult.orderLineResults().get(0).quantity()).isEqualTo(5L),
                    () -> assertThat(orderResult.orderPaymentResult().paymentAmount()).isEqualTo(new BigDecimal("5000.00"))
            );
        }

        @DisplayName("정상적으로 요청하는 경우, 주문이 성공적으로 처리되어야 한다.")
        @Test
        void orderSuccess() {
            User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Point point = Point.from(user.getId());
            point.charge(10000L);
            pointRepository.save(point);
            Product product1 = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product1", BigDecimal.valueOf(1000L), "ON_SALE")));
            stockRepository.save(Stock.create(new StockCommand.Create(product1.getId(), 100L)));
            Product product2 = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product2", BigDecimal.valueOf(2000L), "ON_SALE")));
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
                            .contains(tuple(product1.getId(), 3L, new BigDecimal("1000.00")),
                                    tuple(product2.getId(), 2L, new BigDecimal("2000.00"))),
                    () -> assertThat(order.get().getOrderPayment().getPaymentAmount()).isEqualTo(new BigDecimal("7000.00"))
            );
        }
    }

    @DisplayName("단일 주문 조회 시, 유저 ID가 유효하지 않은 경우, NOT_FOUND 예외를 발생시킨다.")
    @Test
    void throwNotFoundException_whenUserDoesNotExist() {
        CoreException thrown = assertThrows(CoreException.class,
                () -> orderFacade.get(new OrderCriteria.Get(-1L, 1L)));

        assertThat(thrown)
                .usingRecursiveComparison()
                .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }
}
