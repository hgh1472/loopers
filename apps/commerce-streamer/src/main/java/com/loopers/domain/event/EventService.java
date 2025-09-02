package com.loopers.domain.event;

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
        HandledEvent handledEvent = new HandledEvent(cmd.eventId(), cmd.consumerGroup(), cmd.payload());
        try {
            return handledEventRepository.save(handledEvent);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedEventException();
        }
    }
}
