package com.loopers.infrastructure.order;

import com.loopers.domain.order.ExternalDataSender;
import com.loopers.domain.order.Order;
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
public class ExternalDataSenderImpl implements ExternalDataSender {

    private final LoopersDataSender loopersDataSender;

    @Override
    @TimeLimiter(name = "orderDatasender")
    @CircuitBreaker(name = "orderDataSender", fallbackMethod = "sendCircuitFallback")
    public CompletableFuture<Void> send(Order order) {
        return CompletableFuture.runAsync(() -> {
            loopersDataSender.send(order);
        });
    }

    public CompletableFuture<Void> sendCircuitFallback(Order order, Throwable throwable) {
        if (throwable instanceof TimeoutException) {
            log.warn("Loopers에 주문 데이터 전송이 시간 초과되었습니다: 주문 ID={}", order.getId());
        }
        if (throwable instanceof IllegalStateException) {
            log.warn("Loopers에 주문 데이터 전송 중 문제가 발생했습니다: 주문 ID={}", order.getId());
        }
        return CompletableFuture.completedFuture(null);
    }
}
