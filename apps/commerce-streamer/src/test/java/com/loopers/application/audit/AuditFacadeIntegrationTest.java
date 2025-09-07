package com.loopers.application.audit;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.loopers.domain.audit.AuditCommand;
import com.loopers.domain.audit.AuditService;
import com.loopers.domain.event.DuplicatedEventException;
import com.loopers.domain.event.EventCommand;
import com.loopers.domain.event.EventService;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
class AuditFacadeIntegrationTest {
    @Autowired
    private AuditFacade auditFacade;
    @MockitoSpyBean
    private EventService eventService;
    @MockitoSpyBean
    private AuditService auditService;

    @Nested
    @DisplayName("감사 로그 저장 시,")
    class Audit {
        @Test
        @DisplayName("중복된 이벤트는 저장되지 않는다.")
        void notSave_whenDuplicatedEvent() throws DuplicatedEventException {
            AuditCriteria.Audit cri = new AuditCriteria.Audit("eventId", "consumerGroup", "payload", "LikeEvent.Liked", ZonedDateTime.now());
            auditFacade.audit(cri);

            auditFacade.audit(cri);

            verify(eventService, times(2)).save(new EventCommand.Save(cri.eventId(), cri.consumerGroup(), cri.payload(), cri.createdAt()));
            verify(auditService, times(1)).save(new AuditCommand.Audit(cri.eventId(), cri.consumerGroup(), cri.payload(), cri.createdAt()));
        }
    }
}
