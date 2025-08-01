package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserCriteria;
import com.loopers.application.user.UserFacade;
import com.loopers.application.user.UserResult;
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
@RequestMapping("/api/v1/users")
public class UserV1Controller implements UserV1ApiSpec {

    private final UserFacade userFacade;

    @PostMapping
    @Override
    public ApiResponse<UserV1Dto.UserResponse> joinUser(@Valid @RequestBody UserV1Dto.JoinRequest joinRequest) {
        UserResult userResult = userFacade.joinUser(joinRequest.toCriteria());
        return ApiResponse.success(UserV1Dto.UserResponse.from(userResult));
    }

    @GetMapping("/me")
    @Override
    public ApiResponse<UserV1Dto.UserResponse> getMyInfo(@RequestHeader("X-USER-ID") Long userId) {
        UserResult userResult = userFacade.getUser(new UserCriteria.Get(userId));
        return ApiResponse.success(UserV1Dto.UserResponse.from(userResult));
    }
}
