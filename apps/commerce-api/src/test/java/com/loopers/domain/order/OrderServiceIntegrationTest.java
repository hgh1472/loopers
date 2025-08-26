package com.loopers.domain.order;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.loopers.utils.DatabaseCleanUp;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@SpringBootTest
@RecordApplicationEvents
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ApplicationEvents applicationEvents;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

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

        @Test
        @DisplayName("주문 생성 시, 주문 생성 이벤트가 발행된다.")
        void publishOrderCreatedEvent() {
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

            List<OrderEvent.Created> events = applicationEvents.stream(OrderEvent.Created.class)
                    .toList();
            assertThat(events.size()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("주문 만료 시,")
    class Expire {

        @Test
        @DisplayName("지정 시간 이전에 생성된 주문 중 CREATED 상태인 주문의 상태가 EXPIRED 상태로 변경된다.")
        void expireCreatedOrdersBefore() {
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
            OrderCommand.Order command1 = new OrderCommand.Order(1L, null, lines, delivery, BigDecimal.valueOf(3000), BigDecimal.valueOf(2000), 0L);
            Order createdOrder = orderRepository.save(Order.of(command1));

            OrderCommand.Order command2 = new OrderCommand.Order(1L, null, lines, delivery, BigDecimal.valueOf(3000), BigDecimal.valueOf(2000), 0L);
            Order pendingOrder = Order.of(command2);
            pendingOrder.pending();
            orderRepository.save(pendingOrder);

            ZonedDateTime time = ZonedDateTime.now();

            OrderCommand.Order command3 = new OrderCommand.Order(1L, null, lines, delivery, BigDecimal.valueOf(3000), BigDecimal.valueOf(2000), 0L);
            orderRepository.save(Order.of(command3));

            List<OrderInfo> expiredOrders = orderService.expireCreatedOrdersBefore(new OrderCommand.Expire(time));

            assertAll(
                    () -> assertThat(expiredOrders).hasSize(1),
                    () -> assertThat(expiredOrders.get(0).id()).isEqualTo(createdOrder.getId())
            );
        }
    }
}
