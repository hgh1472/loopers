package com.loopers.infrastructure.audit;

import com.loopers.domain.audit.AuditRepository;
import com.loopers.domain.audit.EventLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuditRepositoryImpl implements AuditRepository {
    private final EventLogJpaRepository eventLogJpaRepository;

    @Override
    public EventLog save(EventLog event) {
        return eventLogJpaRepository.save(event);
    }
}
