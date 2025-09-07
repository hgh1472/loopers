package com.loopers.interfaces.consumer.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.application.audit.AuditCriteria;
import com.loopers.message.KafkaMessage;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventMapper {
    private final ObjectMapper objectMapper;
    @Value("${kafka.topics.like}")
    private String likeTopic;
    @Value("${kafka.topics.order-paid}")
    private String paidTopic;
    @Value("${kafka.topics.product-viewed}")
    private String viewedTopic;
    @Value("${kafka.topics.cache-evict-command}")
    private String evictTopic;

    public AuditCriteria.Audit map(String topic, Object object, String group) throws IOException {
        KafkaMessage<?> message = objectMapper.readValue((byte[]) object, KafkaMessage.class);
        return new AuditCriteria.Audit(message.getEventId(), group, message.getPayload().toString(),
                message.getPayload().getClass().getName(), message.getTimestamp());
    }
}
