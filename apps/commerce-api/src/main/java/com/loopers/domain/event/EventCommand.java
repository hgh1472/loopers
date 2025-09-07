package com.loopers.domain.event;

import java.time.ZonedDateTime;

public class EventCommand {
    public record Save(
            String eventId,
            String topic,
            String aggregateId,
            String payload,
            ZonedDateTime createdAt
    ) {
    }
}
