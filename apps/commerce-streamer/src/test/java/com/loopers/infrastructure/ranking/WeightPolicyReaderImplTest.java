package com.loopers.infrastructure.ranking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.loopers.domain.ranking.Weight;
import com.loopers.domain.ranking.WeightPolicyReader;
import com.loopers.utils.DatabaseCleanUp;
import com.loopers.utils.RedisCleanUp;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
class WeightPolicyReaderImplTest {
    @Autowired
    private WeightPolicyReader weightPolicyReader;
    @MockitoSpyBean
    private WeightJpaRepository weightJpaRepository;
    @Autowired
    private RedisCleanUp redisCleanUp;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        redisCleanUp.truncateAll();
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("가중치 조회 시,")
    class Get {
        @Test
        @DisplayName("Redis에서 좋아요 가중치 조회에 실패하면, DB에서 조회한다.")
        void fallbackToDatabase_whenRedisFails() {
            Weight weight = new Weight(0.2, 0.1, 0.7);
            weight.activate();
            weightJpaRepository.save(weight);

            Double likeWeight = weightPolicyReader.getLikeWeight(LocalDate.now());

            verify(weightJpaRepository, times(1)).findByActivateTrue();
            assertThat(likeWeight).isEqualTo(0.2);
        }

        @Test
        @DisplayName("Redis에서 조회수 가중치 조회에 실패하면, DB에서 조회한다.")
        void fallbackToDatabase_whenRedisFails2() {
            Weight weight = new Weight(0.2, 0.1, 0.7);
            weight.activate();
            weightJpaRepository.save(weight);

            Double viewWeight = weightPolicyReader.getViewWeight(LocalDate.now());

            verify(weightJpaRepository, times(1)).findByActivateTrue();
            assertThat(viewWeight).isEqualTo(0.1);
        }

        @Test
        @DisplayName("Redis에서 판매량 가중치 조회에 실패하면, DB에서 조회한다.")
        void fallbackToDatabase_whenRedisFails3() {
            Weight weight = new Weight(0.2, 0.1, 0.7);
            weight.activate();
            weightJpaRepository.save(weight);

            Double salesWeight = weightPolicyReader.getSalesWeight(LocalDate.now());

            verify(weightJpaRepository, times(1)).findByActivateTrue();
            assertThat(salesWeight).isEqualTo(0.7);
        }
    }
}
