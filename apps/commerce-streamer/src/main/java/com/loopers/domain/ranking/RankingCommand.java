package com.loopers.domain.ranking;

import java.time.LocalDate;

public class RankingCommand {
    public record Like(
            Long productId,
            Long count,
            LocalDate date
    ) {
    }

    public record View(
            Long productId,
            Long count,
            LocalDate date
    ) {
    }

    public record Sale(
            Long productId,
            Long count,
            LocalDate date
    ) {
    }

    public record UpdateDailyRanking(
            LocalDate date
    ) {
    }
}
