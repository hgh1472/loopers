package com.loopers.interfaces.api.user;

import com.loopers.application.user.JoinRequest;
import com.loopers.application.user.UserInfo;
import com.loopers.domain.user.UserService;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.user.UserV1Dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserV1Controller implements UserV1ApiSpec {

    private final UserService userService;

    @PostMapping
    @Override
    public ApiResponse<UserResponse> joinUser(@RequestBody JoinRequest joinRequest) {
        UserInfo userInfo = userService.join(joinRequest);
        return ApiResponse.success(UserResponse.from(userInfo));
    }
}
