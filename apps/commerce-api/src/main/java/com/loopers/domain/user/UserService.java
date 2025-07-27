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
        if (userRepository.findByLoginId(command.toLoginId()).isPresent()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이미 가입된 ID입니다.");
        }
        return UserInfo.from(userRepository.save(User.create(command)));
    }

    @Transactional(readOnly = true)
    public UserInfo findUser(Long userId) {
        return userRepository.findById(userId)
                .map(UserInfo::from)
                .orElse(null);
    }
}
