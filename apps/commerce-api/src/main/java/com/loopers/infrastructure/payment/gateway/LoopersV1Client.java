package com.loopers.infrastructure.payment.gateway;

import com.loopers.interfaces.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "loopers-client", url = "${client.loopers.base-url}")
public interface LoopersV1Client {

    String USER_ID_HEADER = "X-USER-ID";

    @PostMapping(path = "/api/v1/payments", consumes = "application/json")
    ApiResponse<LoopersResponse.Request> request(@RequestBody LoopersRequest.Request request,
                                                 @RequestHeader(USER_ID_HEADER) String userId);
}
