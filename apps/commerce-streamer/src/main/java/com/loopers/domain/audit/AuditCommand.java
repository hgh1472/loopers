package com.loopers.domain.audit;

import java.time.ZonedDateTime;

public class AuditCommand {
    public record Audit(
            String eventId,
            String eventName,
            String payload,
            ZonedDateTime createdAt
    ) {
    }
}
