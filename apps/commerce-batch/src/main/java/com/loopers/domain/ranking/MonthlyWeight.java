package com.loopers.domain.ranking;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MonthlyWeight {
    WEEK_0(1.0),
    WEEK_1(0.8),
    WEEK_2(0.5),
    WEEK_3(0.2),
    OTHER(0.0);

    private final double weight;

    public double applyWeight(double score) {
        return score * weight;
    }

    public static MonthlyWeight fromWeeksDiff(int weeksDiff) {
        return switch (weeksDiff) {
            case 0 -> WEEK_0;
            case 1 -> WEEK_1;
            case 2 -> WEEK_2;
            case 3 -> WEEK_3;
            default -> OTHER;
        };
    }
}
