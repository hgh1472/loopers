package com.loopers.infrastructure.product;

import com.loopers.application.product.ProductApplicationEvent;
import com.loopers.application.product.ProductApplicationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCoreEventPublisher implements ProductApplicationEventPublisher {
    private final ApplicationEventPublisher publisher;


    @Override
    public void publish(ProductApplicationEvent.Viewed event) {
        publisher.publishEvent(event);
    }
}
