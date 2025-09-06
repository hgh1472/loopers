package com.loopers.application.audit;

import java.time.ZonedDateTime;

public class AuditCriteria {
    public record Audit(
            String eventId,
            String consumerGroup,
            String payload,
            String eventName,
            ZonedDateTime createdAt
    ) {
    }
}
