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
    public PointInfo initialize(Long userId) {
        Point point = Point.from(userId);
        if (pointRepository.existsByUserId(point.getUserId())) {
            throw new CoreException(ErrorType.CONFLICT, "이미 회원의 포인트가 존재합니다.");
        }
        return PointInfo.from(pointRepository.save(point));
    }

    @Transactional(readOnly = true)
    public PointInfo findPoint(Long userId) {
        return pointRepository.findByUserId(userId)
                .map(PointInfo::from)
                .orElse(null);
    }

    @Transactional
    public PointInfo charge(PointCommand.Charge command) {
        Point point = pointRepository.findByUserId(command.userId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 사용자입니다."));
        point.charge(command.point());
        return PointInfo.from(point);
    }
}
