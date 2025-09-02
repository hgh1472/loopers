package com.loopers.domain.event;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "handled_event", uniqueConstraints = {
        @UniqueConstraint(name = "uk_event_consumer", columnNames = {"event_id", "consumer_group"})
})
public class HandledEvent extends BaseEntity {

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "consumer_group", nullable = false)
    private String consumerGroup;

    @Column(name = "payload", nullable = false)
    private String payload;

    protected HandledEvent() {
    }

    public HandledEvent(String eventId, String consumerGroup, String payload) {
        this.eventId = eventId;
        this.consumerGroup = consumerGroup;
        this.payload = payload;
    }

}
