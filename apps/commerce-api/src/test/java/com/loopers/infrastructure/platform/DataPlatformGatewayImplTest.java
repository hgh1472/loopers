package com.loopers.infrastructure.platform;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.loopers.domain.platform.PlatformCommand;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
class DataPlatformGatewayImplTest {
    @MockitoSpyBean
    private DataPlatformGatewayImpl dataPlatformGateway;
    @MockitoBean
    private LoopersPlatformSender loopersDataSender;


    @Nested
    @DisplayName("주문 데이터 외부 전송 시,")
    class SendOrder {
        @Test
        @DisplayName("1000ms가 초과할 경우, TimeLimiter가 동작한다.")
        void timeout_over1000ms() throws ExecutionException, InterruptedException {
            PlatformCommand.Order command = new PlatformCommand.Order(UUID.randomUUID(), PlatformCommand.Order.Status.CREATED);
            doAnswer(invocation -> {
                Thread.sleep(1300);
                return null;
            }).when(loopersDataSender).send(any(PlatformCommand.Order.class));

            dataPlatformGateway.send(command).get();

            verify(dataPlatformGateway, times(1)).sendOrderCircuitFallback(any(PlatformCommand.Order.class), any(TimeoutException.class));
        }

        @Test
        @DisplayName("IllegalStateException이 발생할 경우, CircuitBreaker가 동작한다.")
        void circuitbreaker_whenCoreException() throws ExecutionException, InterruptedException {
            PlatformCommand.Order command = new PlatformCommand.Order(UUID.randomUUID(), PlatformCommand.Order.Status.CREATED);

            doAnswer(invocation -> {
                throw new IllegalStateException();
            }).when(loopersDataSender).send(any(PlatformCommand.Order.class));

            dataPlatformGateway.send(command).get();

            verify(dataPlatformGateway, times(1)).sendOrderCircuitFallback(any(PlatformCommand.Order.class), any(IllegalStateException.class));
        }
    }

    @Nested
    @DisplayName("주문 데이터 외부 전송 시,")
    class SendPayment {
        @Test
        @DisplayName("1000ms가 초과할 경우, TimeLimiter가 동작한다.")
        void timeout_over1000ms() throws ExecutionException, InterruptedException {
            PlatformCommand.Payment command = new PlatformCommand.Payment(UUID.randomUUID(), "TX-KEY", PlatformCommand.Payment.Status.FAILED);
            doAnswer(invocation -> {
                Thread.sleep(1300);
                return null;
            }).when(loopersDataSender).send(any(PlatformCommand.Payment.class));

            dataPlatformGateway.send(command).get();

            verify(dataPlatformGateway, times(1)).sendPaymentCircuitFallback(any(PlatformCommand.Payment.class), any(TimeoutException.class));
        }

        @Test
        @DisplayName("IllegalStateException이 발생할 경우, CircuitBreaker가 동작한다.")
        void circuitbreaker_whenCoreException() throws ExecutionException, InterruptedException {
            PlatformCommand.Payment command = new PlatformCommand.Payment(UUID.randomUUID(), "TX-KEY", PlatformCommand.Payment.Status.FAILED);

            doAnswer(invocation -> {
                throw new IllegalStateException();
            }).when(loopersDataSender).send(any(PlatformCommand.Payment.class));

            dataPlatformGateway.send(command).get();

            verify(dataPlatformGateway, times(1)).sendPaymentCircuitFallback(any(PlatformCommand.Payment.class), any(IllegalStateException.class));
        }
    }
}
