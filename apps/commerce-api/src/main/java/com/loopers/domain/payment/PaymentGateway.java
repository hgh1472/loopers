package com.loopers.domain.payment;

public interface PaymentGateway {

    GatewayResponse.Request request(Payment payment);

    GatewayResponse.Transaction getTransaction(Payment payment);
}
