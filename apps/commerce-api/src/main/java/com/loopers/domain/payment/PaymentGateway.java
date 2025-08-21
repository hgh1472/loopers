package com.loopers.domain.payment;

public interface PaymentGateway {

    GatewayResponse.Request  request(Payment payment, Card card);

    GatewayResponse.Transaction getTransaction(Payment payment);
}
