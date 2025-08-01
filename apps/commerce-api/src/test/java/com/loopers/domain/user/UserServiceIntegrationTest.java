package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    @MockitoSpyBean
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("회원 가입 시,")
    @Nested
    class Join {
        @DisplayName("유저가 저장된다.")
        @Test
        void join() {
            UserCommand.Join command = new UserCommand.Join("hgh1472", "hgh1472@loopers.com", "1999-06-23", "MALE");

            UserInfo userInfo = userService.join(command);

            verify(userRepository).save(any());
            assertAll(
                    () -> assertThat(userInfo.id()).isNotNull(),
                    () -> assertThat(userInfo.loginId()).isEqualTo(command.loginId()),
                    () -> assertThat(userInfo.email()).isEqualTo(command.email()),
                    () -> assertThat(userInfo.birthDate()).isEqualTo(LocalDate.parse(command.birthDate())),
                    () -> assertThat(userInfo.gender()).isEqualTo(command.gender())
            );
        }

        @DisplayName("이미 가입된 ID로 시도할 경우, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void join_withDuplicateLoginId() {
            UserCommand.Join command = new UserCommand.Join("hgh1472", "hgh1472@loopers.com", "1999-06-23", "MALE");
            userService.join(command);
            UserCommand.Join duplicatedRequest = new UserCommand.Join("hgh1472", "hgh1472@naver.com", "1999-06-23", "MALE");

            CoreException thrown = assertThrows(CoreException.class, () -> userService.join(duplicatedRequest));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "이미 가입된 ID입니다."));
        }

        @DisplayName("이미 가입된 이메일로 시도할 경우, CONFLICT 예외를 발생시킨다.")
        @Test
        void throwsConflictException_whenEmailAlreadyExists() {
            UserCommand.Join command = new UserCommand.Join("hgh1472", "hgh1472@loopers.com", "1999-06-23", "MALE");
            userService.join(command);
            UserCommand.Join duplicatedRequest = new UserCommand.Join("user2", "hgh1472@loopers.com", "1999-06-23", "MALE");

            CoreException thrown = assertThrows(CoreException.class, () -> userService.join(duplicatedRequest));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "이미 가입된 이메일입니다."));
        }
    }

    @DisplayName("회원 정보 조회 시,")
    @Nested
    class Find {
        @DisplayName("해당 ID 회원이 존재할 경우, 회원 정보가 반환된다.")
        @Test
        void getUserInfo() {
            User savedUser = userRepository.save(
                    User.create(new UserCommand.Join("hgh1472", "hgh1472@loopers.com", "1999-06-23", "MALE")));

            UserInfo userInfo = userService.findUser(new UserCommand.Find(savedUser.getId()));

            assertAll(
                    () -> assertThat(userInfo.loginId()).isEqualTo(savedUser.getLoginId().getId()),
                    () -> assertThat(userInfo.email()).isEqualTo(savedUser.getEmail().getAddress()),
                    () -> assertThat(userInfo.birthDate()).isEqualTo(savedUser.getBirthDate().getDate()),
                    () -> assertThat(userInfo.gender()).isEqualTo(savedUser.getGender().name())
            );
        }

        @DisplayName("해당 ID 회원이 존재하지 않는 경우, null이 반환된다.")
        @Test
        void getUserInfo_withNotFoundUserId() {
            UserInfo userInfo = userService.findUser(new UserCommand.Find(-1L));

            assertThat(userInfo).isNull();
        }
    }
}
