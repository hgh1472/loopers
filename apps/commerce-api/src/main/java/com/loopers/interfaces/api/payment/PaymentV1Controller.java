package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.PaymentCriteria;
import com.loopers.application.payment.PaymentFacade;
import com.loopers.application.payment.PaymentResult;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentService;
import com.loopers.interfaces.api.ApiResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentV1Controller implements PaymentV1ApiSpec {

    private final PaymentFacade paymentFacade;
    private final PaymentService paymentService;

    @Override
    @PostMapping("/callback")
    public ApiResponse<?> callback(@RequestBody PaymentV1Dto.CallbackRequest request) {
        switch (request.status()) {
            case SUCCESS -> paymentService.success(new PaymentCommand.Success(request.transactionKey()));
            case FAILED -> paymentFacade.fail(
                    new PaymentCriteria.Fail(request.transactionKey(), UUID.fromString(request.orderId()), request.reason()));
        }
        return ApiResponse.success();
    }

    @Override
    @PostMapping
    public ApiResponse<PaymentV1Dto.PaymentResponse> requestPayment(@RequestBody PaymentV1Dto.PaymentRequest request,
                                                                    @RequestHeader("X-USER-ID") Long userId) {
        PaymentCriteria.Pay criteria =
                new PaymentCriteria.Pay(request.orderId(), request.cardType(), request.cardNo());
        PaymentResult result = paymentFacade.pay(criteria);
        return ApiResponse.success(PaymentV1Dto.PaymentResponse.from(result));
    }
}
