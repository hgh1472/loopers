package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderCriteria;
import com.loopers.application.order.OrderFacade;
import com.loopers.application.order.OrderResult;
import com.loopers.interfaces.api.ApiResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderV1ApiController implements OrderV1ApiSpec {

    private final OrderFacade orderFacade;

    @Override
    @PostMapping
    public ApiResponse<OrderV1Dto.OrderResponse> order(@RequestBody OrderV1Dto.OrderRequest orderRequest,
                                                       @RequestHeader("X-USER-ID") Long userId) {
        List<OrderCriteria.Line> lines = orderRequest.lines().stream()
                .map(OrderV1Dto.Line::toCriteriaLine)
                .toList();
        OrderResult orderResult = orderFacade.order(
                new OrderCriteria.Order(userId, lines, orderRequest.delivery().toCriteriaDelivery(), orderRequest.couponId(),
                        orderRequest.point(), orderRequest.cardType(), orderRequest.cardNo()));
        return ApiResponse.success(OrderV1Dto.OrderResponse.from(orderResult));
    }

    @Override
    @GetMapping("/{orderId}")
    public ApiResponse<OrderV1Dto.OrderResponse> get(@RequestHeader("X-USER-ID") Long userId, @PathVariable UUID orderId) {
        OrderResult result = orderFacade.get(new OrderCriteria.Get(userId, orderId));
        return ApiResponse.success(OrderV1Dto.OrderResponse.from(result));
    }

    @Override
    @GetMapping
    public ApiResponse<List<OrderV1Dto.OrderResponse>> getOrders(@RequestHeader("X-USER-ID") Long userId) {
        List<OrderV1Dto.OrderResponse> response = orderFacade.getOrdersOf(new OrderCriteria.GetOrders(userId))
                .stream()
                .map(OrderV1Dto.OrderResponse::from)
                .toList();

        return ApiResponse.success(response);
    }


}
