package com.loopers.domain.point;

public class PointCommand {

    public record Initialize(Long userId) {
    }

    public record Find(Long userId) {
    }

    public record Charge(Long userId, Long amount) {
    }

    public record Use(Long userId, Long amount) {
    }
}
