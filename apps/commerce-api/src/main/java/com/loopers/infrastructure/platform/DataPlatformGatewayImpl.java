package com.loopers.infrastructure.platform;

import com.loopers.domain.platform.DataPlatformGateway;
import com.loopers.domain.platform.PlatformCommand;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataPlatformGatewayImpl implements DataPlatformGateway {
    private final LoopersPlatformSender loopersPlatformSender;

    @Override
    @TimeLimiter(name = "dataSender")
    @CircuitBreaker(name = "dataSender", fallbackMethod = "sendOrderCircuitFallback")
    public CompletableFuture<Void> send(PlatformCommand.Order order) {
        return CompletableFuture.runAsync(() -> {
            loopersPlatformSender.send(order);
        });
    }

    @Override
    @TimeLimiter(name = "dataSender")
    @CircuitBreaker(name = "dataSender", fallbackMethod = "sendPaymentCircuitFallback")
    public CompletableFuture<Void> send(PlatformCommand.Payment payment) {
        return CompletableFuture.runAsync(() -> {
            loopersPlatformSender.send(payment);
        });
    }

    public CompletableFuture<Void> sendOrderCircuitFallback(PlatformCommand.Order order, Throwable throwable) {
        if (throwable instanceof TimeoutException) {
            log.warn("Loopers 주문 데이터 전송이 시간 초과되었습니다:주문ID={}, 상태={}", order.orderId(), order.status());
        }
        if (throwable instanceof IllegalStateException) {
            log.warn("Loopers 주문 데이터 전송 중 문제가 발생했습니다:주문ID={}, 상태={}", order.orderId(), order.status());
        }
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> sendPaymentCircuitFallback(PlatformCommand.Payment payment, Throwable throwable) {
        if (throwable instanceof TimeoutException) {
            log.warn("Loopers 결제 데이터 전송이 시간 초과되었습니다: 트랜잭션 키={}, 상태={}", payment.transactionKey(), payment.status());
        }
        if (throwable instanceof IllegalStateException) {
            log.warn("Loopers 결제 데이터 전송 중 문제가 발생했습니다: 트랜잭션 키={}, 상태={}", payment.transactionKey(), payment.status());
        }
        return CompletableFuture.completedFuture(null);
    }
}
