package com.loopers.interfaces.scheduler;

import com.loopers.domain.event.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {
    private final OutboxService outboxService;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void retryFailedEvents() {
        outboxService.retryFailedEvents();
    }
}
