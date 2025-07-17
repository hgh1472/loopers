package com.loopers.interfaces.api;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserCommand.Join;
import com.loopers.domain.user.UserRepository;
import com.loopers.interfaces.api.point.PointV1Dto;
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
public class PointV1ApiE2ETest {
    private final TestRestTemplate testRestTemplate;
    private final PointRepository pointRepository;
    private final UserRepository userRepository;
    private final DatabaseCleanUp databaseCleanUp;

    @Autowired
    public PointV1ApiE2ETest(TestRestTemplate testRestTemplate,
                             PointRepository pointRepository,
                             UserRepository userRepository,
                             DatabaseCleanUp databaseCleanUp) {
        this.testRestTemplate = testRestTemplate;
        this.pointRepository = pointRepository;
        this.userRepository = userRepository;
        this.databaseCleanUp = databaseCleanUp;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("GET /api/v1/points")
    class GetPoints {
        @DisplayName("포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.")
        @Test
        void getPoints() {
            User saved = userRepository.save(User.create(new Join("hgh1472", "hgh1472@loopers.com", "1999-06-23", "MALE")));
            Point point = Point.from(saved.getId());
            point.charge(1000L);
            pointRepository.save(point);

            String requestUrl = "/api/v1/points";
            ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-USER-ID", saved.getLoginId().getId());

            ResponseEntity<ApiResponse<PointV1Dto.PointResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(headers), responseType);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().point()).isEqualTo(point.getValue())
            );
        }

        @DisplayName("X-USER-ID 헤더가 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        void getPoints_whenNoUserId() {
            String requestUrl = "/api/v1/points";
            ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            HttpHeaders headers = new HttpHeaders();

            ResponseEntity<ApiResponse<PointV1Dto.PointResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(headers), responseType);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    class ChargePoint {
        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.")
        @Test
        void chargePoint() {
            User saved = userRepository.save(User.create(new Join("hgh1472", "hgh1472@loopers.com", "1999-06-23", "MALE")));
            Point point = Point.from(saved.getId());
            point.charge(1000L);
            pointRepository.save(point);

            String requestUrl = "/api/v1/points/charge";
            ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-USER-ID", saved.getLoginId().getId());
            PointV1Dto.ChargeRequest chargeRequest = new PointV1Dto.ChargeRequest(1000L);

            ResponseEntity<ApiResponse<PointV1Dto.PointResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(chargeRequest, headers), responseType);

            assertAll(
                    () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                    () -> assertThat(response.getBody().data().point()).isEqualTo(point.getValue() + chargeRequest.point())
            );
        }

        @DisplayName("존재하지 않는 유저로 요청할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void chargePoint_whenNonExistUser() {
            String requestUrl = "/api/v1/points/charge";
            ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-USER-ID", "NonExist");
            PointV1Dto.ChargeRequest chargeRequest = new PointV1Dto.ChargeRequest(1000L);

            ResponseEntity<ApiResponse<PointV1Dto.PointResponse>> response =
                    testRestTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(chargeRequest, headers), responseType);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}
