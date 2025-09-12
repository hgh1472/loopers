package com.loopers.interfaces.scheduler;

import com.loopers.domain.ranking.RankingCommand;
import com.loopers.domain.ranking.RankingService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingScheduler {
    private final RankingService rankingService;

    @Scheduled(cron = "0 50 23 * * ?")
    public void updateDailyRankings() {
        LocalDate today = LocalDate.now();
        rankingService.updateDailyRankings(new RankingCommand.UpdateDailyRanking(today));
    }
}
