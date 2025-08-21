package com.loopers.infrastructure.payment.gateway;

import com.loopers.domain.payment.GatewayResponse;
import com.loopers.domain.payment.Payment;
import java.util.UUID;

public class LoopersResponse {
    public record Request(
            String transactionKey,
            String status,
            String reason
    ) {
    }

    public record Transaction(
            String transactionKey,
            String orderId,
            String cardType,
            String cardNo,
            Long amount,
            Status status,
            String reason
    ) {
        public enum Status {
            PENDING,
            SUCCESS,
            FAILED
        }

        public GatewayResponse.Transaction toGatewayResponse() {
            return switch (status) {
                case PENDING -> new GatewayResponse.Transaction(Payment.Status.PENDING, transactionKey, UUID.fromString(orderId),
                        amount, reason);
                case SUCCESS ->
                        new GatewayResponse.Transaction(Payment.Status.COMPLETED, transactionKey, UUID.fromString(orderId),
                                amount, reason);
                case FAILED -> new GatewayResponse.Transaction(Payment.Status.FAILED, transactionKey, UUID.fromString(orderId),
                        amount, reason);
            };
        }
    }
}
