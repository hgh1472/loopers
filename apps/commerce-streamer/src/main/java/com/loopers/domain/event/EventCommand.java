package com.loopers.domain.event;

public class EventCommand {
    public record Save(
            String eventId,
            String consumerGroup,
            String payload
    ) {
    }
}
