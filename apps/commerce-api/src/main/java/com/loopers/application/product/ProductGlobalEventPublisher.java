package com.loopers.application.product;

import org.springframework.stereotype.Component;

@Component
public interface ProductGlobalEventPublisher {
    void publish(ProductGlobalEvent.Viewed event);
}
