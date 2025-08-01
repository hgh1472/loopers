package com.loopers.infrastructure.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointHistory;
import com.loopers.domain.point.PointRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {
    private final PointJpaRepository pointJpaRepository;

    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    @Override
    public Point save(Point point) {
        return pointJpaRepository.save(point);
    }

    @Override
    public Optional<Point> findByUserId(Long userId) {
        return pointJpaRepository.findByUserId(userId);
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return pointJpaRepository.existsByUserId(userId);
    }

    @Override
    public PointHistory record(PointHistory pointHistory) {
        return pointHistoryJpaRepository.save(pointHistory);
    }
}
