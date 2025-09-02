package com.loopers.application.metrics;

import com.loopers.domain.event.DuplicatedEventException;
import com.loopers.domain.event.EventCommand;
import com.loopers.domain.event.EventService;
import com.loopers.domain.metrics.MetricCommand;
import com.loopers.domain.metrics.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MetricsFacade {
    private final MetricsService metricsService;
    private final EventService eventService;

    public void incrementLikeCount(MetricCriteria.IncrementLike cri) {
        try {
            eventService.save(new EventCommand.Save(cri.eventId(), cri.consumerGroup(), cri.payload()));
        } catch (DuplicatedEventException ignored) {
            return;
        }
        metricsService.incrementLikeCount(new MetricCommand.IncrementLike(cri.productId()));
    }

    public void decrementLikeCount(MetricCriteria.DecrementLike cri) {
        try {
            eventService.save(new EventCommand.Save(cri.eventId(), cri.consumerGroup(), cri.payload()));
        } catch (DuplicatedEventException ignored) {
            return;
        }
        metricsService.decrementLikeCount(new MetricCommand.DecrementLike(cri.productId()));
    }
}
