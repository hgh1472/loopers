package com.loopers.domain.event;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EventService {
    private final HandledEventRepository handledEventRepository;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public HandledEvent save(EventCommand.Save cmd) throws DuplicatedEventException {
        HandledEvent handledEvent = new HandledEvent(cmd.eventId(), cmd.consumerGroup(), cmd.payload(), cmd.createdAt());
        try {
            return handledEventRepository.save(handledEvent);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedEventException();
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<HandledEvent> saveAll(List<EventCommand.Save> cmd) throws DuplicatedEventException {
        List<HandledEvent> handledEvents = cmd.stream()
                .map(c -> new HandledEvent(c.eventId(), c.consumerGroup(), c.payload(), c.createdAt()))
                .toList();
        try {
            return handledEventRepository.saveAll(handledEvents);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedEventException();
        }
    }
}
