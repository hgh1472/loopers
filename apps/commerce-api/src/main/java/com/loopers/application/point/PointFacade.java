package com.loopers.application.point;

import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointInfo;
import com.loopers.domain.point.PointService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PointFacade {
    private final PointService pointService;

    public PointResult getPoint(PointCriteria.Get criteria) {
        PointInfo pointInfo = pointService.findPoint(new PointCommand.Find(criteria.userId()));
        if (pointInfo == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "사용자의 포인트 정보를 찾을 수 없습니다.");
        }
        return PointResult.from(pointInfo);
    }

    @Transactional
    public PointResult charge(PointCriteria.Charge criteria) {
        PointInfo pointInfo = pointService.charge(criteria.toCommand());
        return PointResult.from(pointInfo);
    }
}
