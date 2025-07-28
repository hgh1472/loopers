package com.loopers.domain.point;

public class PointCommand {
    public record Charge(Long userId, Long amount) {
    }

    public record Use(Long userId, Long amount) {
    }
}
