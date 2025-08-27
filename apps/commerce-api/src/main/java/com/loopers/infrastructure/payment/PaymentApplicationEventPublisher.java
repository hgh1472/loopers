package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.PaymentEvent;
import com.loopers.domain.payment.PaymentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentApplicationEventPublisher implements PaymentEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(PaymentEvent.Success event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publish(PaymentEvent.Fail event) {
        eventPublisher.publishEvent(event);
    }
}
