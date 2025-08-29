package com.loopers.interfaces.scheduler;

import com.loopers.application.order.OrderCriteria;
import com.loopers.application.order.OrderFacade;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreatedOrderScheduler {

    private final OrderFacade orderFacade;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void processCreatedOrders() {
        ZonedDateTime now = ZonedDateTime.now();
        orderFacade.cancelCreatedOrdersBefore(new OrderCriteria.Expire(now.minusMinutes(5)));
    }
}
