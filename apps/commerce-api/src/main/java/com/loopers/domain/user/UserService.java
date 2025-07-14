package com.loopers.domain.user;

import com.loopers.application.user.JoinRequest;
import com.loopers.application.user.UserInfo;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserService {
    private final UserRepository userRepository;

    public UserInfo join(JoinRequest request) {
        if (userRepository.findByLoginId(request.toLoginId()).isPresent()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이미 가입된 ID입니다.");
        }
        User save = userRepository.save(User.create(request));
        return UserInfo.from(save);
    }
}
