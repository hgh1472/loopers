package com.loopers.domain.ranking;

import java.time.LocalDate;

public class RankingCommand {
    public record DailyRanking(
            int size,
            int page,
            LocalDate date
    ) {
    }
}
