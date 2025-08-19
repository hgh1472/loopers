package com.loopers.domain.payment;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Embeddable
@EqualsAndHashCode
public class Card {

    @Embedded
    private CardNo cardNo;

    @Enumerated(EnumType.STRING)
    private Type type;

    protected Card() {
    }

    private Card(CardNo cardNo, Type type) {
        this.cardNo = cardNo;
        this.type = type;
    }

    public static Card of(String cardNo, String cardType) {
        return new Card(new CardNo(cardNo), Type.from(cardType));
    }

    public enum Type {
        SAMSUNG,
        KB,
        HYUNDAI;

        public static Type from(String type) {
            try {
                return Type.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CoreException(ErrorType.BAD_REQUEST, "지원하지 않는 카드사입니다.");
            }
        }
    }
}
