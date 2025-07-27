package com.loopers.application.point;

import com.loopers.domain.point.PointCommand;

public class PointCriteria {
    public record Charge(Long userId, Long point) {

        public PointCommand.Charge toCommand() {
            return new PointCommand.Charge(
                    userId,
                    point
            );
        }
    }
}
