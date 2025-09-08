package com.loopers.application.metrics;

import static java.util.stream.Collectors.groupingBy;

import com.loopers.domain.event.DuplicatedEventException;
import com.loopers.domain.event.EventCommand;
import com.loopers.domain.event.EventService;
import com.loopers.domain.metrics.MetricCommand;
import com.loopers.domain.metrics.MetricsService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    public void incrementSalesCounts(List<MetricCriteria.IncrementSales> cri) {
        try {
            List<EventCommand.Save> commands = cri.stream()
                    .map(c -> new EventCommand.Save(c.eventId(), c.consumerGroup(), c.payload(), c.createdAt()))
                    .toList();
            eventService.saveAll(commands);
        } catch (DuplicatedEventException ignored) {
            return;
        }

        Map<Long, Long> quantityByProduct = cri.stream()
                .flatMap(criteria -> criteria.lines().stream())
                .collect(Collectors.toMap(
                        MetricCriteria.SaleLine::productId,
                        MetricCriteria.SaleLine::quantity,
                        Long::sum
                ));

        LocalDate createdAt = cri.isEmpty() ? LocalDate.now() : cri.getFirst().createdAt().toLocalDate();

        List<MetricCommand.IncrementSales> commands = quantityByProduct.entrySet().stream()
                .map(e -> new MetricCommand.IncrementSales(e.getKey(), e.getValue(), createdAt))
                .toList();

        metricsService.incrementSalesCounts(commands);
    }

    public void incrementViewCounts(List<MetricCriteria.IncrementView> cri) {
        try {
            List<EventCommand.Save> commands = cri.stream()
                    .map(c -> new EventCommand.Save(c.eventId(), c.consumerGroup(), c.payload(), c.createdAt()))
                    .toList();
            eventService.saveAll(commands);
        } catch (DuplicatedEventException ignored) {
            return;
        }

        List<MetricCommand.IncrementView> commands = cri.stream()
                .collect(groupingBy(MetricCriteria.IncrementView::productId))
                .entrySet().stream()
                .map(entry -> new MetricCommand.IncrementView(
                        entry.getKey(),
                        (long) entry.getValue().size(),
                        entry.getValue().getFirst().createdAt().toLocalDate()
                ))
                .toList();

        metricsService.incrementViewCounts(commands);
    }
}
