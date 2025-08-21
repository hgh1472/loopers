package com.loopers.infrastructure.payment.gateway;

import com.loopers.infrastructure.payment.gateway.LoopersResponse.Transaction;
import com.loopers.interfaces.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "loopers-get-client", url = "${client.loopers.base-url}")
public interface LoopersGetV1Client {

    String USER_ID_HEADER = "X-USER-ID";


    @GetMapping("/api/v1/payments/{transactionKey}")
    ApiResponse<Transaction> getTransaction(@PathVariable String transactionKey,
                                            @RequestHeader(USER_ID_HEADER) String userId);
}
