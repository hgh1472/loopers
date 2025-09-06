package com.loopers.interfaces.consumer.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.application.audit.AuditCriteria;
import com.loopers.interfaces.consumer.events.CacheEvent;
import com.loopers.interfaces.consumer.events.LikeEvent;
import com.loopers.interfaces.consumer.events.OrderEvent;
import com.loopers.interfaces.consumer.events.ProductEvent;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
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

    public AuditCriteria.Audit map(String topic, Object object, String group) {
        try {
            if (topic.equals(likeTopic)) {
                LikeEvent.Like e = objectMapper.readValue((byte[]) object, LikeEvent.Like.class);
                return new AuditCriteria.Audit(e.eventId(), group, e.toString(), e.getClass().getName(), e.createdAt());
            }
            if (topic.equals(paidTopic)) {
                OrderEvent.Paid e = objectMapper.readValue((byte[]) object, OrderEvent.Paid.class);
                return new AuditCriteria.Audit(e.eventId(), group, e.toString(), e.getClass().getName(), e.createdAt());
            }
            if (topic.equals(viewedTopic)) {
                ProductEvent.Viewed e = objectMapper.readValue((byte[]) object, ProductEvent.Viewed.class);
                return new AuditCriteria.Audit(e.eventId(), group, e.toString(), e.getClass().getName(), e.createdAt());
            }
            if (topic.equals(evictTopic)) {
                CacheEvent.ProductEvict e = objectMapper.readValue((byte[]) object, CacheEvent.ProductEvict.class);
                return new AuditCriteria.Audit(e.eventId(), group, e.toString(), e.getClass().getName(), e.createdAt());
            }
        } catch (IOException e) {
            log.error("토픽 {} 매핑 중 오류 발생: {}", topic, e.getMessage());
            throw new CoreException(ErrorType.BAD_REQUEST, "이벤트 매핑 중 오류가 발생했습니다.");
        }
        throw new CoreException(ErrorType.BAD_REQUEST, "알 수 없는 토픽입니다: " + topic);
    }
}
