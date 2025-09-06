package com.loopers.interfaces.consumer.audit;

import com.loopers.application.audit.AuditCriteria;
import com.loopers.application.audit.AuditFacade;
import io.confluent.parallelconsumer.ParallelConsumerOptions;
import io.confluent.parallelconsumer.ParallelStreamProcessor;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
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
                                                                  KafkaTemplate<Object, Object> kafkaTemplate,
                                                                  @Value("${kafka.topics.like}") String likeTopic,
                                                                  @Value("${kafka.topics.order-paid}") String orderPaidTopic,
                                                                  @Value("${kafka.topics.product-viewed}") String viewedTopic,
                                                                  @Value("${kafka.topics.cache-evict-command}") String evictTopic
    ) {
        ParallelStreamProcessor<Object, Object> processor = ParallelStreamProcessor.createEosStreamProcessor(options);

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);

        processor.subscribe(List.of(likeTopic, orderPaidTopic, viewedTopic, evictTopic));
        processor.poll(record -> {
            ConsumerRecord<Object, Object> consumerRecord = record.getSingleRecord().getConsumerRecord();
            try {
                // Parallel Consumer의 Batch Option이 없을 때 가능
                AuditCriteria.Audit cri = eventMapper.map(consumerRecord.topic(), consumerRecord.value(), AUDIT_CONSUMER_GROUP);
                auditFacade.audit(cri);
            } catch (RuntimeException e) {
                log.error("메세지 처리 중 문제가 발생했습니다. topic: {}, value : {},  e: {}",
                        consumerRecord.topic(), consumerRecord.value(), e.getMessage());
                recoverer.accept(consumerRecord, e);
            } catch (IOException e) {
                log.error("지원하지 않는 메시지 형식입니다. value: {}, topic: {}", consumerRecord.value(), consumerRecord.topic());
            }
        });
        return processor;
    }
}
