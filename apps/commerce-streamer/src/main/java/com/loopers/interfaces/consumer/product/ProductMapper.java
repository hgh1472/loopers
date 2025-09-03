package com.loopers.interfaces.consumer.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final ObjectMapper objectMapper;

    public Long map(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, ProductId.class).productId();
        } catch (JsonProcessingException e) {
            log.error("잘못된 이벤트 형식입니다. topic: {}", topic);
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ProductId(Long productId) {
    }
}
