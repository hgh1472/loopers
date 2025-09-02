package com.loopers.domain.event;

import java.time.ZonedDateTime;

public class EventCommand {
    public record Save(
            String eventId,
            String consumerGroup,
            String payload,
            ZonedDateTime createdAt
    ) {
    }
}
