package com.loopers.domain.event;

import java.util.List;

public interface HandledEventRepository {
    HandledEvent save(HandledEvent handledEvent);

    List<HandledEvent> saveAll(List<HandledEvent> handledEvents);
}
