package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserInfo join(UserCommand.Join command) {
        User user = User.create(command);
        if (userRepository.existsBy(user.getLoginId())) {
            throw new CoreException(ErrorType.CONFLICT, "이미 가입된 ID입니다.");
        }
        if (userRepository.existsBy(user.getEmail())) {
            throw new CoreException(ErrorType.CONFLICT, "이미 가입된 이메일입니다.");
        }
        return UserInfo.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserInfo findUser(Long userId) {
        if (userId == null) {
            return null;
        }

        return userRepository.findById(userId)
                .map(UserInfo::from)
                .orElse(null);
    }
}
