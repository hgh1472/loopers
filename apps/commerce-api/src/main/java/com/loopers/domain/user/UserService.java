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
    public User join(UserCommand.Join command) {
        if (userRepository.findByLoginId(command.toLoginId()).isPresent()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이미 가입된 ID입니다.");
        }
        return userRepository.save(User.create(command));
    }

    @Transactional(readOnly = true)
    public User getUser(LoginId loginId) {
        return userRepository.findByLoginId(loginId).orElse(null);
    }

    @Transactional(readOnly = true)
    public Long getPoints(LoginId loginId) {
        return userRepository.findByLoginId(loginId)
                .map(User::getPoint)
                .orElse(null);
    }

    @Transactional
    public Long chargePoint(UserCommand.Charge command) {
        User user = userRepository.findByLoginIdWithLock(command.toLoginId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, String.format("%s 사용자를 찾을 수 없습니다.", command.loginId())));
        user.chargePoint(command.point());
        return user.getPoint();
    }
}
