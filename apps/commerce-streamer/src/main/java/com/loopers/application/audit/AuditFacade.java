package com.loopers.application.audit;

import com.loopers.domain.audit.AuditCommand;
import com.loopers.domain.audit.AuditService;
import com.loopers.domain.event.DuplicatedEventException;
import com.loopers.domain.event.EventCommand;
import com.loopers.domain.event.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditFacade {
    private final AuditService auditService;
    private final EventService eventService;

    public void audit(AuditCriteria.Audit cri) {
        try {
            eventService.save(new EventCommand.Save(cri.eventId(), cri.consumerGroup(), cri.payload(), cri.createdAt()));
        } catch (DuplicatedEventException e) {
            return;
        }
        auditService.save(new AuditCommand.Audit(cri.eventId(), cri.consumerGroup(), cri.payload(), cri.createdAt()));
    }
}
