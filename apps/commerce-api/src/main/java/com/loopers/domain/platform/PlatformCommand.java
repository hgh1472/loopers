package com.loopers.domain.platform;

import java.util.UUID;

public class PlatformCommand {

    public record Order(
            UUID orderId,
            Order.Status status
    ) {
        public enum Status {
            CREATED, FAILED, PAID, EXPIRED
        }
    }

    public record Payment(
            UUID orderId,
            String transactionKey,
            Payment.Status status
    ) {
        public enum Status {
            FAILED, SUCCESS, REFUND
        }
    }
}
