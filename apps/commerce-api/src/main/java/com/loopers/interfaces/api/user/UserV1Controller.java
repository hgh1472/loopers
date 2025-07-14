package com.loopers.interfaces.api.user;

import com.loopers.application.user.JoinRequest;
import com.loopers.application.user.UserFacade;
import com.loopers.application.user.UserInfo;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.user.UserV1Dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserV1Controller implements UserV1ApiSpec {

    private final UserFacade userFacade;

    @PostMapping
    @Override
    public ApiResponse<UserResponse> joinUser(@RequestBody JoinRequest joinRequest) {
        UserInfo userInfo = userFacade.joinUser(joinRequest);
        return ApiResponse.success(UserResponse.from(userInfo));
    }

    @GetMapping("/me")
    @Override
    public ApiResponse<UserResponse> getMyInfo(@RequestHeader("X-USER-ID") String id) {
        UserInfo userInfo = userFacade.getUserInfo(id);
        return ApiResponse.success(UserResponse.from(userInfo));
    }
}
