package com.loopers.domain.payment;

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
}
