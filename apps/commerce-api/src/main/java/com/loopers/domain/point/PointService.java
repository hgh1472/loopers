package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PointService {
    private final PointRepository pointRepository;

    @Transactional
    public Point initialize(Long userId) {
        Point point = Point.from(userId);
        return pointRepository.save(point);
    }

    @Transactional(readOnly = true)
    public Point getPoint(Long userId) {
        return pointRepository.findByUserId(userId)
                .orElse(null);
    }

    @Transactional
    public Point charge(PointCommand.Charge command) {
        Point point = pointRepository.findByUserIdWithLock(command.userId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 사용자입니다."));
        point.charge(command.point());
        return point;
    }
}
