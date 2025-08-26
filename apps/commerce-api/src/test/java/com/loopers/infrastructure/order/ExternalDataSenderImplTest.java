package com.loopers.infrastructure.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderCommand.Line;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
class ExternalDataSenderImplTest {

    @MockitoSpyBean
    private ExternalDataSenderImpl externalDataSender;
    @MockitoBean
    private LoopersDataSender loopersDataSender;

    @Nested
    @DisplayName("주문 데이터 외부 전송 시,")
    class Send {

        @Test
        @DisplayName("1000ms가 초과할 경우, TimeLimiter가 동작한다.")
        void timeout_over1000ms() throws ExecutionException, InterruptedException {
            List<Line> lines = List.of(
                    new OrderCommand.Line(1L, 2L, BigDecimal.valueOf(1000L))
            );
            OrderCommand.Delivery delivery = new OrderCommand.Delivery(
                    "홍길동",
                    "010-1234-5678",
                    "서울시 강남구 역삼동 123-456",
                    "101호",
                    "배송 요청사항"
            );
            OrderCommand.Order command = new OrderCommand.Order(1L, null, lines, delivery, BigDecimal.valueOf(3000), BigDecimal.valueOf(2000), 0L);
            Order order = Order.of(command);
            doAnswer(invocation -> {
                Thread.sleep(1300);
                return null;
            }).when(loopersDataSender).send(any());

            externalDataSender.send(order).get();

            verify(externalDataSender, times(1)).sendCircuitFallback(any(Order.class), any(TimeoutException.class));
        }

        @Test
        @DisplayName("IllegalStateException이 발생할 경우, CircuitBreaker가 동작한다.")
        void circuitbreaker_whenCoreException() throws ExecutionException, InterruptedException {
            List<Line> lines = List.of(
                    new OrderCommand.Line(1L, 2L, BigDecimal.valueOf(1000L))
            );
            OrderCommand.Delivery delivery = new OrderCommand.Delivery(
                    "홍길동",
                    "010-1234-5678",
                    "서울시 강남구 역삼동 123-456",
                    "101호",
                    "배송 요청사항"
            );
            OrderCommand.Order command = new OrderCommand.Order(1L, null, lines, delivery, BigDecimal.valueOf(3000), BigDecimal.valueOf(2000), 0L);
            Order order = Order.of(command);
            doAnswer(invocation -> {
                throw new IllegalStateException();
            }).when(loopersDataSender).send(any());

            externalDataSender.send(order).get();

            verify(externalDataSender, times(1)).sendCircuitFallback(any(Order.class), any(IllegalStateException.class));
        }
    }
}
