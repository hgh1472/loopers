package com.loopers.application.point;

import com.loopers.domain.point.PointCommand;

public class PointCriteria {
    public record Charge(String loginId, Long point) {

        public PointCommand.Charge toCommand(Long userId) {
            return new PointCommand.Charge(
                    userId,
                    point
            );
        }
    }
}
