package com.loopers.interfaces.api.user;

import com.loopers.application.user.PointInfo;
import com.loopers.application.user.UserFacade;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/points")
public class PointV1Controller {
    private final UserFacade userFacade;

    @GetMapping
    public ApiResponse<PointV1Dto.PointResponse> getPoints(@RequestHeader("X-USER-ID") String loginId) {
        PointInfo pointInfo = userFacade.getPoints(loginId);
        return ApiResponse.success(new PointV1Dto.PointResponse(pointInfo.point()));
    }
}
