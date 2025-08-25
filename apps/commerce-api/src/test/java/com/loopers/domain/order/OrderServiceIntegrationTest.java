package com.loopers.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.loopers.infrastructure.order.OrderJpaRepository;
import com.loopers.utils.DatabaseCleanUp;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("주문 생성 시,")
    class Create {

        @DisplayName("주문과 주문 항목이 저장된다.")
        @Test
        void saveOrderAndOrderLines() {
            List<OrderCommand.Line> lines = List.of(
                    new OrderCommand.Line(1L, 2L, BigDecimal.valueOf(1000L)),
                    new OrderCommand.Line(2L, 3L, BigDecimal.valueOf(2000L))
            );
            OrderCommand.Delivery delivery = new OrderCommand.Delivery(
                    "홍길동",
                    "010-1234-5678",
                    "서울시 강남구 역삼동 123-456",
                    "101호",
                    "배송 요청사항"
            );
            OrderCommand.Order command = new OrderCommand.Order(1L, null, lines, delivery, BigDecimal.valueOf(3000), BigDecimal.valueOf(2000), 0L);

            OrderInfo orderInfo = orderService.order(command);

            Order order = orderRepository.findById(orderInfo.id()).get();
            List<OrderLine> orderLines = order.getOrderLines();
            assertAll(
                    () -> assertThat(order.getUserId()).isEqualTo(command.userId()),
                    () -> assertThat(orderLines).hasSize(2)
            );
        }

        @DisplayName("주문이 상태는 CREATED 상태로 저장된다.")
        @Test
        void saveOrderWithPendingStatus() {
            List<OrderCommand.Line> lines = List.of(
                    new OrderCommand.Line(1L, 2L, BigDecimal.valueOf(1000L)),
                    new OrderCommand.Line(2L, 3L, BigDecimal.valueOf(2000L))
            );
            OrderCommand.Delivery delivery = new OrderCommand.Delivery(
                    "홍길동",
                    "010-1234-5678",
                    "서울시 강남구 역삼동 123-456",
                    "101호",
                    "배송 요청사항"
            );
            OrderCommand.Order command = new OrderCommand.Order(1L, null, lines, delivery, BigDecimal.valueOf(3000), BigDecimal.valueOf(2000), 0L);

            OrderInfo orderInfo = orderService.order(command);

            assertThat(orderInfo.orderStatus()).isEqualTo("CREATED");
        }
    }
}
