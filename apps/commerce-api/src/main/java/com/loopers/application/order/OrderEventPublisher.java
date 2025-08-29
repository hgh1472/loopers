package com.loopers.application.order;

public interface OrderEventPublisher {
    void publish(OrderApplicationEvent.Refund event);

    void publish(OrderApplicationEvent.Expired event);

    void publish(OrderApplicationEvent.Paid event);

    void publish(OrderApplicationEvent.Created event);
}
