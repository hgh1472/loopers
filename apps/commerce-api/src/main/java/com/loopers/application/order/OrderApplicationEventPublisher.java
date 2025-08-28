package com.loopers.application.order;

public interface OrderApplicationEventPublisher {
    void publish(OrderApplicationEvent.Refund event);

    void publish(OrderApplicationEvent.Expired event);

    void publish(OrderApplicationEvent.Paid event);
}
