package com.loopers.interfaces.api.user;

import com.loopers.application.user.PointInfo;
import com.loopers.application.user.UserCriteria.Charge;
import com.loopers.application.user.UserFacade;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/points")
public class PointV1Controller implements PointV1ApiSpec {
    private final UserFacade userFacade;

    @GetMapping
    @Override
    public ApiResponse<PointV1Dto.PointResponse> getPoints(@RequestHeader("X-USER-ID") String loginId) {
        PointInfo pointInfo = userFacade.getPoints(loginId);
        return ApiResponse.success(PointV1Dto.PointResponse.from(pointInfo));
    }

    @PostMapping("/charge")
    @Override
    public ApiResponse<PointV1Dto.PointResponse> chargePoint(@RequestHeader("X-USER-ID") String loginId,
                                                             @RequestBody PointV1Dto.ChargeRequest chargeRequest) {
        PointInfo pointInfo = userFacade.chargePoint(new Charge(loginId, chargeRequest.point()));
        return ApiResponse.success(PointV1Dto.PointResponse.from(pointInfo));
    }
}
