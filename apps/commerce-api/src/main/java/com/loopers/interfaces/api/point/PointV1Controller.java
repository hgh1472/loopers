package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointFacade;
import com.loopers.application.point.PointInfo;
import com.loopers.interfaces.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/points")
public class PointV1Controller implements PointV1ApiSpec {
    private final PointFacade pointFacade;

    @GetMapping
    @Override
    public ApiResponse<PointV1Dto.PointResponse> getPoints(@RequestHeader("X-USER-ID") String loginId) {
        PointInfo pointInfo = pointFacade.getPoint(loginId);
        return ApiResponse.success(PointV1Dto.PointResponse.from(pointInfo));
    }

    @PostMapping("/charge")
    @Override
    public ApiResponse<PointV1Dto.PointResponse> chargePoint(@RequestHeader("X-USER-ID") String loginId,
                                                             @Valid @RequestBody PointV1Dto.ChargeRequest chargeRequest) {
        PointInfo pointInfo = pointFacade.charge(chargeRequest.toCriteria(loginId));
        return ApiResponse.success(PointV1Dto.PointResponse.from(pointInfo));
    }
}
