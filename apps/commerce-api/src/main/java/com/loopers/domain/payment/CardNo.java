package com.loopers.domain.payment;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

@Embeddable
@EqualsAndHashCode
public class CardNo {

    @Column(name = "card_no", nullable = false)
    private String value;

    protected CardNo() {
    }

    public CardNo(String value) {
        if (value == null || value.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "카드 번호를 입력해주세요.");
        }
        if (!value.matches("\\d{4}-\\d{4}-\\d{4}-\\d{4}")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "카드 번호 형식이 올바르지 않습니다.");
        }
        this.value = value;
    }

    public String value() {
        return value;
    }
}
