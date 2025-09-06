package com.loopers.message;

import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class KafkaMessage<T> {
    private String eventId;
    private String aggregateId;
    private String topic;
    private ZonedDateTime timestamp;
    private Integer version;
    private T payload;

    public static <T> KafkaMessage<T> of(String aggregateId, String topic, T payload) {
        return KafkaMessage.<T>builder()
                .eventId(UUID.randomUUID().toString())
                .topic(topic)
                .timestamp(ZonedDateTime.now())
                .version(1)
                .aggregateId(aggregateId)
                .payload(payload)
                .build();
    }
}
