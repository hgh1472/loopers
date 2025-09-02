package com.loopers.domain.audit;

public class AuditCommand {
    public record Audit(
            String eventId,
            String eventName,
            String payload
    ) {
    }
}
