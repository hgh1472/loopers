package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Nested
    @DisplayName("유저를 생성할 때,")
    class Create {
        @DisplayName("이미 존재하는 ID가 주어지면, CONFLICT 예외가 발생한다.")
        @Test
        void throwsConflictException_whenLoginIdAlreadyExists() {
            UserCommand.Join command = new UserCommand.Join("exist", "email@email.com", "1999-06-23", "MALE");
            given(userRepository.existsBy(command.toLoginId()))
                    .willReturn(true);

            CoreException thrown = assertThrows(CoreException.class, () -> userService.join(command));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "이미 가입된 ID입니다."));
        }

        @DisplayName("이미 존재하는 Email이 주어지면, CONFLICT 예외가 발생한다.")
        @Test
        void throwsConflictException_whenEmailAlreadyExists() {
            UserCommand.Join command = new UserCommand.Join("exist", "email@email.com", "1999-06-23", "MALE");
            given(userRepository.existsBy(command.toLoginId()))
                    .willReturn(false);
            given(userRepository.existsBy(command.toEmail()))
                    .willReturn(true);

            CoreException thrown = assertThrows(CoreException.class, () -> userService.join(command));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "이미 가입된 이메일입니다."));
        }
    }

    @Nested
    @DisplayName("유저 정보를 조회할 때,")
    class Get {
        @DisplayName("존재하지 않는 유저 ID로 조회하면, null을 반환한다.")
        @Test
        void throwsNotFoundException_whenNonExistId() {
            given(userRepository.findById(-1L))
                    .willReturn(Optional.empty());

            UserInfo userInfo = userService.findUser(-1L);

            assertThat(userInfo).isNull();
        }
    }
}
