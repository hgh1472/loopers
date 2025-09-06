package com.loopers.domain.audit;

import java.time.ZonedDateTime;

public record EventLogInfo(
        String eventId,
        String eventName,
        String payload,
        ZonedDateTime createdAt
) {
    public static EventLogInfo from(EventLog eventLog) {
        return new EventLogInfo(
                eventLog.getEventId(),
                eventLog.getEventName(),
                eventLog.getPayload(),
                eventLog.getCreatedAt()
        );
    }
}
