package com.loopers.interfaces.api;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserRepository;
import com.loopers.interfaces.api.user.UserV1Dto;
import com.loopers.interfaces.api.user.UserV1Dto.UserResponse;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserV1ApiE2ETest {

    private final TestRestTemplate testRestTemplate;
    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final DatabaseCleanUp databaseCleanUp;

    @Autowired
    public UserV1ApiE2ETest(TestRestTemplate testRestTemplate,
                            UserRepository userRepository,
                            PointRepository pointRepository,
                            DatabaseCleanUp databaseCleanUp) {
        this.testRestTemplate = testRestTemplate;
        this.userRepository = userRepository;
        this.pointRepository = pointRepository;
        this.databaseCleanUp = databaseCleanUp;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("POST /api/v1/user")
    @Nested
    class Post {
        @DisplayName("회원가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
        @Test
        void returnUserInfo_success() {
            UserV1Dto.JoinRequest joinRequest = new UserV1Dto.JoinRequest("hgh1472", "hgh1472@loopers.com", "1999-06-23", "MALE");
            String requestUrl = "/api/v1/users";
            ParameterizedTypeReference<ApiResponse<UserResponse>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<UserResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(joinRequest), responseType);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().loginId()).isEqualTo(joinRequest.loginId()),
                    () -> assertThat(response.getBody().data().email()).isEqualTo(joinRequest.email()),
                    () -> assertThat(response.getBody().data().birthDate()).isEqualTo(joinRequest.birthDate()),
                    () -> assertThat(response.getBody().data().gender()).isEqualTo(joinRequest.gender())
            );
        }

        @DisplayName("회원가입 시 성별이 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        void throwBadRequest_whenNoGender() {
            UserV1Dto.JoinRequest joinRequest = new UserV1Dto.JoinRequest("hgh1472", "hgh1472@loopers.com", "1999-06-23", null);
            String requestUrl = "/api/v1/users";
            ParameterizedTypeReference<ApiResponse<UserResponse>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<UserResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(joinRequest), responseType);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }
    }

    @DisplayName("GET /api/v1/users/me")
    @Nested
    class GetUser {
        @DisplayName("내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.")
        @Test
        void getMyInfo() {
            User saved = userRepository.save(User.create(new UserCommand.Join("hgh1472", "hgh1472@loopers.com", "1999-06-23", "MALE")));
            Point savedPoint = pointRepository.save(Point.from(saved.getId()));

            String requestUrl = "/api/v1/users/me";
            ParameterizedTypeReference<ApiResponse<UserResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-USER-ID", saved.getLoginId().getId());

            ResponseEntity<ApiResponse<UserResponse>> response = testRestTemplate.exchange(requestUrl, HttpMethod.GET,
                    new HttpEntity<>(headers), responseType);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().id()).isEqualTo(saved.getId()),
                    () -> assertThat(response.getBody().data().loginId()).isEqualTo(saved.getLoginId().getId()),
                    () -> assertThat(response.getBody().data().email()).isEqualTo(saved.getEmail().getAddress()),
                    () -> assertThat(response.getBody().data().birthDate()).isEqualTo(saved.getBirthDate().getDate().toString()),
                    () -> assertThat(response.getBody().data().gender()).isEqualTo(saved.getGender().name()),
                    () -> assertThat(response.getBody().data().point()).isEqualTo(savedPoint.getValue())
            );
        }

        @DisplayName("존재하지 않는 ID 로 조회할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void getMyInfo_withNonExistId() {
            String requestUrl = "/api/v1/users/me";
            ParameterizedTypeReference<ApiResponse<UserResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-USER-ID", "NONEXIST");

            ResponseEntity<ApiResponse<UserResponse>> response = testRestTemplate.exchange(requestUrl, HttpMethod.GET,
                    new HttpEntity<>(headers), responseType);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }
    }
}
