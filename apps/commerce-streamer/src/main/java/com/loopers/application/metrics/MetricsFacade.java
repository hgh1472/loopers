package com.loopers.application.metrics;

import com.loopers.domain.event.DuplicatedEventException;
import com.loopers.domain.event.EventCommand;
import com.loopers.domain.event.EventService;
import com.loopers.domain.metrics.MetricCommand;
import com.loopers.domain.metrics.MetricsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MetricsFacade {
    private final MetricsService metricsService;
    private final EventService eventService;

    public void incrementLikeCount(MetricCriteria.IncrementLike cri) {
        try {
            eventService.save(new EventCommand.Save(cri.eventId(), cri.consumerGroup(), cri.payload(), cri.createdAt()));
        } catch (DuplicatedEventException ignored) {
            return;
        }
        metricsService.incrementLikeCount(new MetricCommand.IncrementLike(cri.productId(), cri.createdAt().toLocalDate()));
    }

    public void decrementLikeCount(MetricCriteria.DecrementLike cri) {
        try {
            eventService.save(new EventCommand.Save(cri.eventId(), cri.consumerGroup(), cri.payload(), cri.createdAt()));
        } catch (DuplicatedEventException ignored) {
            return;
        }
        metricsService.decrementLikeCount(new MetricCommand.DecrementLike(cri.productId(), cri.createdAt().toLocalDate()));
    }

    public void incrementSalesCount(MetricCriteria.IncrementSales cri) {
        try {
            eventService.save(new EventCommand.Save(cri.eventId(), cri.consumerGroup(), cri.payload(), cri.createdAt()));
        } catch (DuplicatedEventException ignored) {
            return;
        }
        List<MetricCommand.SaleLine> saleLines = cri.lines().stream()
                .map(line -> new MetricCommand.SaleLine(line.productId(), line.quantity()))
                .toList();

        metricsService.incrementSalesCount(new MetricCommand.IncrementSales(saleLines, cri.createdAt().toLocalDate()));
    }

    public void incrementViewCount(MetricCriteria.IncrementView cri) {
        try {
            eventService.save(new EventCommand.Save(cri.eventId(), cri.consumerGroup(), cri.payload(), cri.createdAt()));
        } catch (DuplicatedEventException ignored) {
            return;
        }
        metricsService.incrementViewCount(new MetricCommand.IncrementView(cri.productId(), cri.createdAt().toLocalDate()));
    }
}
