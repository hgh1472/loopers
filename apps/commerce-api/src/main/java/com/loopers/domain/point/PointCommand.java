package com.loopers.domain.point;

public class PointCommand {
    public record Charge(Long userId, Long point) {
    }
}
