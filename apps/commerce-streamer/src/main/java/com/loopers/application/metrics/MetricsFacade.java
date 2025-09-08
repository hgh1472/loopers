package com.loopers.application.metrics;

import static java.util.stream.Collectors.groupingBy;

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

    public void incrementLikeCounts(List<MetricCriteria.IncrementLike> cri) {
        try {
            List<EventCommand.Save> commands = cri.stream()
                    .map(c -> new EventCommand.Save(c.eventId(), c.consumerGroup(), c.payload(), c.createdAt()))
                    .toList();
            eventService.saveAll(commands);
        } catch (DuplicatedEventException ignored) {
            return;
        }
        List<MetricCommand.IncrementLikes> commands = cri.stream()
                .collect(groupingBy(MetricCriteria.IncrementLike::productId))
                .entrySet().stream()
                .map(entry -> new MetricCommand.IncrementLikes(
                        entry.getKey(),
                        (long) entry.getValue().size(),
                        entry.getValue().getFirst().createdAt().toLocalDate()
                ))
                .toList();

        metricsService.incrementLikeCounts(commands);
    }

    public void decrementLikeCounts(List<MetricCriteria.DecrementLike> cri) {
        try {
            List<EventCommand.Save> commands = cri.stream()
                    .map(c -> new EventCommand.Save(c.eventId(), c.consumerGroup(), c.payload(), c.createdAt()))
                    .toList();
            eventService.saveAll(commands);
        } catch (DuplicatedEventException ignored) {
            return;
        }
        List<MetricCommand.DecrementLikes> commands = cri.stream()
                .collect(groupingBy(MetricCriteria.DecrementLike::productId))
                .entrySet().stream()
                .map(entry -> new MetricCommand.DecrementLikes(
                        entry.getKey(),
                        (long) entry.getValue().size(),
                        entry.getValue().getFirst().createdAt().toLocalDate()
                ))
                .toList();

        metricsService.decrementLikeCounts(commands);
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
