package com.loopers.interfaces.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.payment.GatewayResponse;
import com.loopers.domain.payment.PaymentGateway;
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
import com.loopers.interfaces.api.order.OrderV1Dto;
import com.loopers.utils.DatabaseCleanUp;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
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
public class OrderV1ApiE2ETest {
    private final TestRestTemplate testRestTemplate;
    private final DatabaseCleanUp databaseCleanUp;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final PointRepository pointRepository;
    private final OrderRepository orderRepository;
    @MockitoBean
    private PaymentGateway paymentGateway;

    @Autowired
    public OrderV1ApiE2ETest(TestRestTemplate testRestTemplate, DatabaseCleanUp databaseCleanUp,
                             ProductRepository productRepository, UserRepository userRepository,
                             StockRepository stockRepository, PointRepository pointRepository,
                             OrderRepository orderRepository) {
        this.testRestTemplate = testRestTemplate;
        this.databaseCleanUp = databaseCleanUp;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.pointRepository = pointRepository;
        this.orderRepository = orderRepository;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("POST /api/v1/orders")
    class OrderRequest {
        final String URL = "/api/v1/orders";

        @DisplayName("사용자 ID가 유효하지 않은 경우, NOT_FOUND 예외를 반환한다.")
        @Test
        void returnNotFoundException_whenInvalidUser() {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("X-USER-ID", "9999999"); // 존재하지 않는 사용자 ID
            List<OrderV1Dto.Line> lines = List.of(new OrderV1Dto.Line(1L, 2L));
            OrderV1Dto.Delivery delivery = new OrderV1Dto.Delivery("황건하", "010-1234-5678", "서울특별시 강남구 테헤란로 123", "1층 101호", "요구사항");
            OrderV1Dto.OrderRequest request = new OrderV1Dto.OrderRequest(lines, delivery, null, 0L, "SAMSUNG", "0000-0000-0000-0000");
            ParameterizedTypeReference<ApiResponse<Void>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<Void>> response =
                    testRestTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(request, httpHeaders), responseType);

            assertAll(
                    () -> assertThat(response.getStatusCode().is4xxClientError()).isTrue()
            );
        }

        @DisplayName("주문을 성공적으로 생성할 경우, 주문 정보를 반환한다.")
        @Test
        void returnOrderResponse() {
            given(paymentGateway.request(any())).willReturn(new GatewayResponse.Request(true, "TX-KEY"));
            User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Point point = Point.from(user.getId());
            point.charge(10000L);
            pointRepository.save(point);
            Product product1 = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product1", BigDecimal.valueOf(1000L), "ON_SALE")));
            stockRepository.save(Stock.create(new StockCommand.Create(product1.getId(), 100L)));
            Product product2 = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product2", BigDecimal.valueOf(2000L), "ON_SALE")));
            stockRepository.save(Stock.create(new StockCommand.Create(product2.getId(), 100L)));

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("X-USER-ID", String.valueOf(user.getId()));
            List<OrderV1Dto.Line> lines = List.of(new OrderV1Dto.Line(product1.getId(), 2L), new OrderV1Dto.Line(product2.getId(), 3L));
            OrderV1Dto.Delivery delivery = new OrderV1Dto.Delivery("황건하", "010-1234-5678", "서울특별시 강남구 테헤란로 123", "1층 101호", "요구사항");
            OrderV1Dto.OrderRequest request = new OrderV1Dto.OrderRequest(lines, delivery, null, 0L, "SAMSUNG", "0000-0000-0000-0000");
            ParameterizedTypeReference<ApiResponse<OrderV1Dto.OrderResponse>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<OrderV1Dto.OrderResponse>> response =
                    testRestTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(request, httpHeaders), responseType);

            Optional<Order> order = orderRepository.findById(response.getBody().data().orderId());
            assertAll(
                    () -> assertThat(order).isPresent(),
                    () -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue(),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody().data()).isNotNull(),
                    () -> assertThat(response.getBody().data().orderId()).isEqualTo(order.get().getId()),
                    () -> assertThat(response.getBody().data().lines()).isEqualTo(lines),
                    () -> assertThat(response.getBody().data().payment().paymentAmount().longValue()).isEqualTo(order.get().getOrderPayment().getPaymentAmount().longValue()),
                    () -> assertThat(response.getBody().data().delivery()).isEqualTo(delivery)
            );
        }
    }

    @DisplayName("단일 주문 조회 시, 사용자 주문 정보를 반환한다.")
    @Test
    void returnOrderResponse() {
        User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
        OrderCommand.Delivery delivery = new OrderCommand.Delivery("황건하", "010-1234-5678", "서울특별시 강남구 강남대로 지하396", "강남역 지하 XX", "요구사항");
        List<OrderCommand.Line> lines = List.of(new OrderCommand.Line(1L, 2L, new BigDecimal("3000")));
        OrderCommand.Order cmd = new OrderCommand.Order(1L, null, lines, delivery, new BigDecimal("6000"), new BigDecimal("6000"),  0L);
        Order order = Order.of(cmd);
        Order saved = orderRepository.save(order);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-USER-ID", String.valueOf(user.getId()));
        String url = "/api/v1/orders/" + saved.getId();
        ParameterizedTypeReference<ApiResponse<OrderV1Dto.OrderResponse>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<ApiResponse<OrderV1Dto.OrderResponse>> response =
                testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null, httpHeaders), responseType);

        assertAll(
                () -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue(),
                () -> assertThat(response.getBody()).isNotNull(),
                () -> assertThat(response.getBody().data()).isNotNull(),
                () -> assertThat(response.getBody().data().orderId()).isEqualTo(saved.getId()),
                () -> assertThat(response.getBody().data().lines()).hasSize(1),
                () -> assertThat(response.getBody().data().lines()).contains(new OrderV1Dto.Line(1L, 2L)),
                () -> assertThat(response.getBody().data().payment().paymentAmount().longValue()).isEqualTo(saved.getOrderPayment().getPaymentAmount().longValue()),
                () -> assertThat(response.getBody().data().delivery()).isEqualTo(new OrderV1Dto.Delivery(
                        delivery.receiverName(),
                        delivery.phoneNumber(),
                        delivery.baseAddress(),
                        delivery.detailAddress(),
                        delivery.requirements())
                )
        );
    }

    @DisplayName("주문 목록 조회 시, 사용자의 주문 목록을 반환한다.")
    @Test
    void returnOrderList() {
        User user = userRepository.save(User.create(new UserCommand.Join("test1", "hgh1472@loopers.im", "1999-06-23", "MALE")));
        OrderCommand.Delivery delivery = new OrderCommand.Delivery("황건하", "010-1234-5678", "서울특별시 강남구 강남대로 지하396", "강남역 지하 XX", "요구사항");
        List<OrderCommand.Line> lines1 = List.of(
                new OrderCommand.Line(1L, 2L, new BigDecimal("1000")),
                new OrderCommand.Line(2L, 3L, new BigDecimal("2000")));
        OrderCommand.Order cmd1 = new OrderCommand.Order(1L, null, lines1, delivery, new BigDecimal("8000"), new BigDecimal("8000"), 0L);
        Order order1 = Order.of(cmd1);
        Order saved1 = orderRepository.save(order1);

        List<OrderCommand.Line> lines2 = List.of(
                new OrderCommand.Line(1L, 2L, new BigDecimal("1000")),
                new OrderCommand.Line(2L, 3L, new BigDecimal("2000"))
        );
        OrderCommand.Order cmd2 = new OrderCommand.Order(1L, null, lines2, delivery, new BigDecimal("8000"), new BigDecimal("8000"), 0L);
        Order order2 = Order.of(cmd2);
        Order saved2 = orderRepository.save(order2);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-USER-ID", String.valueOf(user.getId()));
        String url = "/api/v1/orders";
        ParameterizedTypeReference<ApiResponse<List<OrderV1Dto.OrderResponse>>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<ApiResponse<List<OrderV1Dto.OrderResponse>>> response =
                testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null, httpHeaders), responseType);

        assertAll(
                () -> assertThat(response.getStatusCode().is2xxSuccessful()).isTrue(),
                () -> assertThat(response.getBody()).isNotNull(),
                () -> assertThat(response.getBody().data()).isNotNull(),
                () -> assertThat(response.getBody().data()).hasSize(2),
                () -> assertThat(response.getBody().data()).extracting(OrderV1Dto.OrderResponse::orderId)
                        .containsExactlyInAnyOrder(saved1.getId(), saved2.getId()),
                () -> assertThat(response.getBody().data()).extracting(OrderV1Dto.OrderResponse::lines)
                        .allSatisfy(lines -> assertThat(lines).hasSize(2)),
                () -> assertThat(response.getBody().data()).extracting(OrderV1Dto.OrderResponse::payment)
                        .allSatisfy(payment -> assertThat(payment.paymentAmount().longValue())
                                .isEqualTo(saved1.getOrderPayment().getPaymentAmount().longValue()))
        );
    }
}
