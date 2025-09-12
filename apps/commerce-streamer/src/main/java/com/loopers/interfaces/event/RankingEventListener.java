package com.loopers.interfaces.event;

import com.loopers.application.metrics.MetricsApplicationEvent;
import com.loopers.domain.ranking.RankingCommand;
import com.loopers.domain.ranking.RankingCommand.Sale;
import com.loopers.domain.ranking.RankingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RankingEventListener {
    private final RankingService rankingService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(List<MetricsApplicationEvent.Updated> events) {
        switch (events.getFirst().type()) {
            case LIKE -> {
                List<RankingCommand.Like> commands = events.stream()
                        .map(event -> new RankingCommand.Like(event.productId(), event.count(), event.createdAt()))
                        .toList();
                rankingService.recordLikeCounts(commands);
            }
            case VIEW -> {
                List<RankingCommand.View> commands = events.stream()
                        .map(event -> new RankingCommand.View(event.productId(), event.count(), event.createdAt()))
                        .toList();
                rankingService.recordViewCounts(commands);
            }
            case SALES -> {
                List<Sale> commands = events.stream()
                        .map(event -> new Sale(event.productId(), event.count(), event.createdAt()))
                        .toList();
                rankingService.recordSalesCounts(commands);
            }
        }
    }
}
