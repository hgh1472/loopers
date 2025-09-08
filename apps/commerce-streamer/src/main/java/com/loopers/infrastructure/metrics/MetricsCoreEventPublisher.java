package com.loopers.infrastructure.metrics;

import com.loopers.application.metrics.MetricsApplicationEvent;
import com.loopers.application.metrics.MetricsApplicationEventPublisher;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MetricsCoreEventPublisher implements MetricsApplicationEventPublisher {
    private final ApplicationEventPublisher eventPublisher;


    @Override
    public void publish(List<MetricsApplicationEvent.Updated> events) {
        eventPublisher.publishEvent(events);
    }
}
