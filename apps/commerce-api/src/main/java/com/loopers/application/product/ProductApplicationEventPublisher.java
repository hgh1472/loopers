package com.loopers.application.product;

public interface ProductApplicationEventPublisher {
    void publish(ProductApplicationEvent.Viewed event);
}
