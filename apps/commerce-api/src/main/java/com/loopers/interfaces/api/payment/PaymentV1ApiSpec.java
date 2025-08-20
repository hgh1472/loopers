package com.loopers.interfaces.api.payment;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Payment API", description = "Loopers Payment API입니다.")
public interface PaymentV1ApiSpec {

    @Operation(
            summary = "결제 콜백",
            description = "결제 요청에 대한 콜백입니다."
    )
    ApiResponse<?> callback(PaymentV1Dto.CallbackRequest request);
}
