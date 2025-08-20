package com.loopers.infrastructure.payment.gateway;

import com.loopers.domain.payment.GatewayResponse;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentGateway;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoopersPaymentGateway implements PaymentGateway {

    @Value("${client.loopers.user-id}")
    private String userId;
    @Value("${client.loopers.callback-url}")
    private String callbackUrl;
    private final LoopersV1Client loopersV1Client;

    @Override
    public GatewayResponse.Request request(Payment payment) {
        ApiResponse<LoopersResponse.Request> request =
                loopersV1Client.request(LoopersRequest.Request.of(payment, callbackUrl), userId);
        if (request.meta().result().equals(ApiResponse.Metadata.Result.SUCCESS)) {
            return GatewayResponse.Request.success(request.data().transactionKey());
        } else {
            return GatewayResponse.Request.fail();
        }
    }
}
