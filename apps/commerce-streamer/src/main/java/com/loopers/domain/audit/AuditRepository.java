package com.loopers.domain.audit;


public interface AuditRepository {
    EventLog save(EventLog event);
}
