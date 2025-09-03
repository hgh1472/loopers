package com.loopers.interfaces.consumer.product;

import com.loopers.config.kafka.KafkaConfig;
import com.loopers.domain.cache.CacheCommand;
import com.loopers.domain.cache.CacheService;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductKafkaListener {
    private final CacheService cacheService;
    private final ProductMapper productMapper;

    @KafkaListener(
            topics = {"${kafka.topics.order-paid}", "${kafka.topics.liked}", "${kafka.topics.like-canceled}"},
            containerFactory = KafkaConfig.BATCH_LISTENER,
            groupId = "cache-evict-consumer"
    )
    public void consumeEvict(List<ConsumerRecord<String, byte[]>> messages, Acknowledgment acknowledgment) {
        Set<Long> productIds = messages.stream()
                .map(message -> productMapper.map(message.topic(), message.value()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        for (Long productId : productIds) {
            cacheService.evictProductCache(new CacheCommand.EvictProduct(productId));
        }
        acknowledgment.acknowledge();
    }
}
