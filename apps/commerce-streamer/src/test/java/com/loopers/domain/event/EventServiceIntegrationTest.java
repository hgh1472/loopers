package com.loopers.domain.event;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.loopers.utils.DatabaseCleanUp;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EventServiceIntegrationTest {
    @Autowired
    private EventService eventService;
    @Autowired
    private HandledEventRepository handledEventRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("이벤트 저장 시,")
    class Save {
        @Test
        @DisplayName("중복된 이벤트라면, DuplicatedEventException 예외가 발생한다.")
        void throwDuplicatedEventException_whenDuplicatedEvent() {
            String eventId = "event-1";
            String consumerGroup = "group-1";
            String payload = "{}";
            ZonedDateTime now = ZonedDateTime.now();
            handledEventRepository.save(new HandledEvent(eventId, consumerGroup, payload, now));
            EventCommand.Save cmd = new EventCommand.Save(eventId, consumerGroup, payload, now);

            assertThatThrownBy(() -> eventService.save(cmd))
                    .isInstanceOf(DuplicatedEventException.class);
        }
    }
}
