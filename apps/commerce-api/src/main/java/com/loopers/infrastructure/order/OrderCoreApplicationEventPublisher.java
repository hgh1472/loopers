package com.loopers.infrastructure.order;

import com.loopers.application.order.OrderApplicationEvent;
import com.loopers.application.order.OrderApplicationEvent.Paid;
import com.loopers.application.order.OrderApplicationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCoreApplicationEventPublisher implements OrderApplicationEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(OrderApplicationEvent.Refund event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publish(OrderApplicationEvent.Expired event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publish(OrderApplicationEvent.Paid event) {
        eventPublisher.publishEvent(event);
    }
}
