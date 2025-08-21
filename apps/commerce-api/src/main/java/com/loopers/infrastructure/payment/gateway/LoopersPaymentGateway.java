package com.loopers.infrastructure.payment.gateway;

import com.loopers.domain.payment.GatewayResponse;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentGateway;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import io.github.resilience4j.retry.annotation.Retry;
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
    @Retry(name = "pgRequest", fallbackMethod = "requestFallback")
    public GatewayResponse.Request request(Payment payment) {
        ApiResponse<LoopersResponse.Request> request =
                loopersV1Client.request(LoopersRequest.Request.of(payment, callbackUrl), userId);
        if (!request.meta().result().equals(ApiResponse.Metadata.Result.SUCCESS)) {
            throw new CoreException(ErrorType.INTERNAL_ERROR, "결제 요청 실패");
        }
        return GatewayResponse.Request.success(request.data().transactionKey());
    }

    @Override
    public GatewayResponse.Transaction getTransaction(Payment payment) {
        ApiResponse<LoopersResponse.Transaction> response = loopersV1Client.getTransaction(payment.getTransactionKey(), userId);
        return response.data().toGatewayResponse();
    }

    public GatewayResponse.Request requestFallback(Payment payment, Throwable throwable) {
        return GatewayResponse.Request.fail();
    }
}
