package com.loopers.test;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.test.context.event.ApplicationEvents;

/**
 * 애플리케이션 이벤트 왜 할까?
 * Synchronous Concept 보다 더 가독성은 떨어질 수 있다.
 * 그럼 이거 왜 함?
 *
 * "내가 뭘 해야 하는지"(메인 로직) vs "그로 인해 뭘 해야 하는지" (부가 로직)
 * 주문 <-> 서로 인과관계가 있다.
 * 주문 -> 결제를 ? "결제에 의한 결과"는 주문의 일부
 * 하지만 "주문"이 없으면, "결제"도 없다.
 *
 * 쿠폰, 재고, 포인트, 데이터 플랫폼
 * 주문에 관심 있는 사람은 많다.
 * 주문 이 모두를 알아야 하나..? 본 목적에 맞나?
 * -----
 * "의존의 강결합"을 피하자.
 * 주문을 핸들링하는 사람이 결제도 알아야 하나?
 * -----
 *
 * 주문이 결제 말고도 알아야 하면.. 그 사람들 다 케어할거야?
 *
 * Q. 쿠폰의 사용을 나눠야 하나요?
 * 1. 주문 입장에서 쿠폰의 역할? 내 총 주문 금액에서 얼마나 할인된 금액을 얻을 수 있는지?
 * 2. 주문이 쿠폰의 사용까지 알아야함? -> 의사결정 주체마다 다르다.
 *      (2-1) 나는 주문이 쿠폰까지 같은 정합성으로 해결했으면 좋겠어 파
 *             - 주문이 쿠폰의 적용까지, 실제로 사용까지 제어했으면 좋겠어.
 *             - 쿠폰의 사용은 "주문"이 Trigger
 *      (2-2) 나는 주문은 쿠폰이 주는 할인액까지만 알았으면 좋겠어 파.
 *             - 주문의 실행 (쿠폰이 가격을 얼마나 깎을지까지)
 *  *          - 쿠폰의 사용 -> 주문의 "실행"에 따른 Trigger
 *
 *  - 주문이 성공하면, 데이터 플랫폼에 전송한다.
 *    -> 데이터 플랫폼에 전송하는게 실패하면, 주문은 실패해야 되는가? NO
 *    -> 쿠폰의 사용을 실패하면, 주문은 실패해야 되는가? YES or NO
 *  => 명확하게 주문이 어디까지 알아야 하는지,
 *      -> 그 주문이 성공함에 따라 "인과 관계"처럼 발생하는 후처리가 무엇이 있는지
 *  주문의 "메인" 트랜잭션은 어디까지인가? -> (주문 입장에서 쿠폰 사용이 메인 트랜잭션인가? -> 같은 트랜잭션일 수는 있지만, 메인 로직은 아니다.)
 *
 * 각자가 "자기의 책임"에 대해 집중할 수 있다.
 * --> 내 책임은 어디까지야?를 나름의 기준에 따라 정해야 된다.
 *
 * ---> 좋아요 -> like/unlike -> LikeCount
 * 좋아요 실패하면 집계도 실패해야 한다.
 * 그런데 집계가 실패하면 좋아요가 실패되어야 하는건 아니다.
 *
 * "서로가 알아야하는지 고민하자."
 *
 * 내가 생각하는 메인로직/서브로직의 기준은 뭘까?
 *
 * (1) "비즈니스 로직"에서 "메인"과 "서브", 그리고 "서브 1", "서브 2", ...은 뭐가 될 것인가?
 * (2) 그러면 경계에 대한 이유, 경계에 대한 구현이 잘 되었는지?
 * (3) 하나의 과정 -> 하나의 결과
 *     -----> 하나의 과정 (좋아요) --> 결과 (1) 그 상품에 대한 좋아요 집계
 *                             --> 결과 (2) 이 상품에 대해 누군가가 좋아요를 했어요 "기록"
 *                             --> 결과 (3) 검색/추천 모델에서 이 상품이 인기도가 있구나..
 *                             --> 결과 (4) 이 상품에 대한 캐시를 깬다.
 *
 * 쿠폰을 같은 트랜잭션에서 적용하더라도, 주문 이라는 메인 로직에서 쿠폰 사용까지는 몰라도 된다고 생각
 * 쿠폰 사용은 후속 트랜잭션(같은 트랜잭션 BEFORE_COMMIT)에서 처리?
 *
 * 결과가 어떤 상황에 참여할까를 고려할 수 있게 되는 구조 = 이벤트 기반의 구조
 *
 *
 * ApplicationEvents에는 기본적으로 이벤트 하나가 들어가있다. -> 스프링에서의 나 준비되있어의 이벤트
 * OrderApplicationPublisher.publish(~)
 */

@SpringBootTest
public class EventTest {

//    @Autowired
//    private OrderApplicationPublisher orderApplicationPublisher;
//    @Autowired
//    ApplicationEvents applicationEvents;
//
//    @Test
//    void test() {
//        OrderCompleted orderCompleted = new OrderCompleted(1L);
//
//        orderApplicationPublisher.publish(orderCompleted);
//
//        System.out.println(applicationEvents.stream().toList());
//    }


    @Component
    public class OrderApplicationPublisher {
        private final ApplicationEventPublisher publisher;

        public OrderApplicationPublisher(ApplicationEventPublisher publisher) {
            this.publisher = publisher;
        }

        public void publish(OrderCompleted event) {
            publisher.publishEvent(event);
        }
    }
}

class OrderCompleted {
    private Long orderId;

    public OrderCompleted(Long orderId) {
        this.orderId = orderId;
    }
}


