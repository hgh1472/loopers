package com.loopers.infrastructure.payment.gateway;

import com.loopers.domain.payment.GatewayResponse;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentGateway;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoopersPaymentGateway implements PaymentGateway {

    @Value("${client.loopers.user-id}")
    private String userId;
    @Value("${client.loopers.callback-url}")
    private String callbackUrl;
    private final LoopersRequestV1Client loopersRequestV1Client;
    private final LoopersGetV1Client loopersGetV1Client;

    @Override
    @Retry(name = "pgRequest", fallbackMethod = "requestFallback")
    @CircuitBreaker(name = "pgRequest", fallbackMethod = "requestFallback")
    public GatewayResponse.Request request(Payment payment) {
        ApiResponse<LoopersResponse.Request> request =
                loopersRequestV1Client.requestPayment(LoopersRequest.Request.of(payment, callbackUrl), userId);
        if (!request.meta().result().equals(ApiResponse.Metadata.Result.SUCCESS)) {
            throw new CoreException(ErrorType.INTERNAL_ERROR, "결제 요청 실패");
        }
        return GatewayResponse.Request.success(request.data().transactionKey());
    }

    @Override
    @Retry(name = "getTransaction", fallbackMethod = "getTransactionFallback")
    @CircuitBreaker(name = "getTransaction", fallbackMethod = "getTransactionFallback")
    public GatewayResponse.Transaction getTransaction(Payment payment) {
        ApiResponse<LoopersResponse.Transaction> response = loopersGetV1Client.getTransaction(payment.getTransactionKey(),
                userId);
        return response.data().toGatewayResponse();
    }

    public GatewayResponse.Request requestFallback(Payment payment, Throwable throwable) {
        log.error("Loopers PG 결제 요청이 실패하였습니다. {}", throwable.getMessage());
        return GatewayResponse.Request.fail();
    }

    public GatewayResponse.Transaction getTransactionFallback(Payment payment, Throwable throwable) {
        log.error("Loopers PG {} 정보 조회에 실패하였습니다.: {}", payment.getTransactionKey(), throwable.getMessage());
        throw new CoreException(ErrorType.INTERNAL_ERROR, "결제 정보 조회에 실패하였습니다.");
    }
}
