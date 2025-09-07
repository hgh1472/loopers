package com.loopers.interfaces.consumer.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.cache.CacheCommand;
import com.loopers.domain.cache.CacheService;
import com.loopers.interfaces.consumer.events.CacheEvent;
import com.loopers.message.KafkaMessage;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheKafkaListener {
    private final CacheService cacheService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${kafka.topics.cache-evict-command}",
            groupId = "cache-evict-consumer"
    )
    public void consumeEvict(ConsumerRecord<String, byte[]> message, Acknowledgment acknowledgment) throws IOException {
        KafkaMessage<CacheEvent.ProductEvict> event = objectMapper.readValue(message.value(), new TypeReference<>() {
        });
        cacheService.evictProductCache(new CacheCommand.EvictProduct(Long.parseLong(event.getAggregateId())));
        acknowledgment.acknowledge();
    }
}
