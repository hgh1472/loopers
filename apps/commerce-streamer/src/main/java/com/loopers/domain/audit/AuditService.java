package com.loopers.domain.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditRepository auditRepository;

    public EventLogInfo save(AuditCommand.Audit cmd) {
        EventLog eventLog = new EventLog(cmd.eventId(), cmd.eventName(), cmd.payload(), cmd.createdAt());
        return EventLogInfo.from(auditRepository.save(eventLog));
    }
}
