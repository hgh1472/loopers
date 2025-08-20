package com.loopers.infrastructure.payment.gateway;

public class LoopersResponse {
    public record Request(
            String transactionKey,
            String status,
            String reason
    ) {
    }
}
