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
    public PointInfo initialize(PointCommand.Initialize command) {
        Point point = Point.from(command.userId());
        if (pointRepository.existsByUserId(point.getUserId())) {
            throw new CoreException(ErrorType.CONFLICT, "이미 회원의 포인트가 존재합니다.");
        }
        return PointInfo.from(pointRepository.save(point));
    }

    @Transactional(readOnly = true)
    public PointInfo findPoint(PointCommand.Find command) {
        return pointRepository.findByUserId(command.userId())
                .map(PointInfo::from)
                .orElse(null);
    }

    @Transactional
    public PointInfo charge(PointCommand.Charge command) {
        Point point = pointRepository.findByUserIdWithLock(command.userId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 사용자입니다."));
        PointHistory chargeHistory = point.charge(command.amount());
        pointRepository.record(chargeHistory);
        return PointInfo.from(point);
    }

    @Transactional
    public PointInfo use(PointCommand.Use command) {
        Point point = pointRepository.findByUserIdWithLock(command.userId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 사용자입니다."));
        PointHistory useHistory = point.use(command.amount());
        pointRepository.record(useHistory);
        return PointInfo.from(point);
    }
}
