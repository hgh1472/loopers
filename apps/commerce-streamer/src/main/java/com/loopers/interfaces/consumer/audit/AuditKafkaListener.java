package com.loopers.interfaces.consumer.audit;

import com.loopers.application.audit.AuditFacade;
import com.loopers.support.error.CoreException;
import io.confluent.parallelconsumer.ParallelConsumerOptions;
import io.confluent.parallelconsumer.ParallelStreamProcessor;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditKafkaListener {
    private static final String AUDIT_CONSUMER_GROUP = "audit-consumer";
    private final EventMapper eventMapper;
    private final AuditFacade auditFacade;

    @Bean(destroyMethod = "close")
    public ParallelStreamProcessor<Object, Object> auditProcessor(ParallelConsumerOptions<Object, Object> options,
                                                                  @Value("${kafka.topics.liked}") String likedTopic,
                                                                  @Value("${kafka.topics.like-canceled}") String likeCanceledTopic,
                                                                  @Value("${kafka.topics.order-paid}") String orderPaidTopic,
                                                                  @Value("${kafka.topics.product-viewed}") String viewedTopic,
                                                                  @Value("${kafka.topics.cache-evict-command}") String evictTopic
    ) {
        ParallelStreamProcessor<Object, Object> processor = ParallelStreamProcessor.createEosStreamProcessor(options);

        processor.subscribe(List.of(likedTopic, likeCanceledTopic, orderPaidTopic, viewedTopic, evictTopic));
        processor.poll(record -> {
            record.stream()
                    .map(r -> {
                        try {
                            return eventMapper.map(r.topic(), r.value(), AUDIT_CONSUMER_GROUP);
                        } catch (CoreException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .forEach(auditFacade::audit);
        });
        return processor;
    }
}
