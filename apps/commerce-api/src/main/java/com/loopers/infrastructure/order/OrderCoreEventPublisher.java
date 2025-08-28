package com.loopers.infrastructure.order;

import com.loopers.application.order.OrderApplicationEvent;
import com.loopers.application.order.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCoreEventPublisher implements OrderEventPublisher {

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

    @Override
    public void publish(OrderApplicationEvent.Created event) {
        eventPublisher.publishEvent(event);
    }
}
