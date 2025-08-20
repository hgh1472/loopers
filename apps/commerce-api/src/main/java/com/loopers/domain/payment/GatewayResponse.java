package com.loopers.domain.payment;

import java.util.UUID;

public class GatewayResponse {
    public record Request(
            boolean isSuccess,
            String transactionKey
    ) {
        public static Request fail() {
            return new Request(false, null);
        }

        public static Request success(String transactionKey) {
            return new Request(true, transactionKey);
        }
    }

    public record Transaction(
            Payment.Status status,
            String transactionKey,
            UUID orderId,
            String cardType,
            String cardNo,
            Long amount,
            String reason
    ) {
    }
}
