package com.loopers.domain.ranking;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum WeeklyWeight {
    DAY_0(1.0),
    DAY_1(0.9),
    DAY_2(0.8),
    DAY_3(0.7),
    DAY_4(0.4),
    DAY_5(0.2),
    DAY_6(0.1),
    OTHER(0.0);

    private final double weight;

    public double applyWeight(double score) {
        return score * weight;
    }

    public static WeeklyWeight fromDaysDiff(int daysDiff) {
        return switch (daysDiff) {
            case 0 -> DAY_0;
            case 1 -> DAY_1;
            case 2 -> DAY_2;
            case 3 -> DAY_3;
            case 4 -> DAY_4;
            case 5 -> DAY_5;
            case 6 -> DAY_6;
            default -> OTHER;
        };
    }
}
