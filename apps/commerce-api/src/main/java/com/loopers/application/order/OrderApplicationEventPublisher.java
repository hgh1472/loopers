package com.loopers.application.order;

import com.loopers.application.order.OrderApplicationEvent.Refund;

public interface OrderApplicationEventPublisher {
    void publish(Refund event);
}
