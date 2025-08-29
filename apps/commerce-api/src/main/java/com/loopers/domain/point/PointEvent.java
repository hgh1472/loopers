package com.loopers.domain.point;

import com.loopers.support.event.UserActivityEvent;

public class PointEvent {
    public record Charged(
            Long amount,
            Long userId
    ) implements UserActivityEvent {
    }

    public record Used(
            Long amount,
            Long userId
    ) implements UserActivityEvent {
    }
}
