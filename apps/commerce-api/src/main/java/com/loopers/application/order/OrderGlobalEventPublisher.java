package com.loopers.application.order;

public interface OrderGlobalEventPublisher {
    void publish(OrderGlobalEvent.Paid event);
}
