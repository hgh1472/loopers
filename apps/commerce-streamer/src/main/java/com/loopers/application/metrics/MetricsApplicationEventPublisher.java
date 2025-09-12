package com.loopers.application.metrics;

import java.util.List;

public interface MetricsApplicationEventPublisher {
    void publish(List<MetricsApplicationEvent.Updated> events);
}
