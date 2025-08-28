package com.loopers.infrastructure.order;

import com.loopers.application.order.OrderApplicationEvent.Refund;
import com.loopers.application.order.OrderApplicationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCoreApplicationEventPublisher implements OrderApplicationEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(Refund event) {
        eventPublisher.publishEvent(event);
    }
}
