package com.loopers.infrastructure.order;

import com.loopers.domain.order.Order;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoopersDataSender {

    public void send(Order order) {
        try {
            Random random = new Random();
            int ms = random.nextInt(300, 3000);
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new IllegalStateException();
        }
    }

    public CompletableFuture<Void> sendTimeoutFallback(Order order, Throwable throwable) {
        log.warn("Loopers에 주문 데이터 전송이 시간 초과되었습니다: 주문 ID={}", order.getId());
        return CompletableFuture.completedFuture(null);
    }
}
