
> - 시퀀스 다이어그램에서 Controller, Repository와 같은 세부 사항은 생략하였습니다. <br>
> - Facade에서 도메인 간 협력을 나타내었습니다.

# 상품 목록 조회

```mermaid
sequenceDiagram
  Actor A as Actor
  participant PF as ProductFacade
  participant P as Product
  participant L as ProductLike
  participant U as User

  opt 페이지 사이즈 초과
  Note right of A: Controller 내 페이지 유효성 검사
  A->>A: 400 Bad Request
  end
  A->>PF: 상품 목록 조회 요청
  activate PF
  PF->>P: 조회 조건에 해당하는 상품 목록 요청
  activate P
  P-->>PF: 페이지 내 상품 목록 반환
  deactivate P
  PF->>L: 상품 별 좋아요 수 조회
  activate L
  L-->>PF: 상품 별 좋아요 수 반환
  deactivate L
  PF->>U: 사용자 정보 확인
  activate U
  U-->>PF: 로그인 정보 반환
  deactivate U
  opt 로그인한 사용자
    PF->>L: 상품 별 좋아요 여부 조회
    activate L
    L-->>PF: 상품 별 좋아요 여부 반환
  end
  deactivate L
  PF->>A: 상품 목록 반환
  deactivate PF
```

# 상품 상세

```mermaid
sequenceDiagram
  actor A as Actor
  participant PF as ProductFacade
  participant P as Product
  participant S as Stock
  participant L as ProductLike
  participant U as User
  
  A->>PF: 상품 상세 조회 요청
  activate PF
  PF->>P: 상품 정보 조회
  activate P
  P-->>PF: 상품 정보 반환
  deactivate P
  PF->>S: 상품 재고 조회
  activate S
  S-->>PF: 상품 재고 반환
  deactivate S
  PF->>L: 상품 좋아요 수 조회
  activate L
  L-->>PF: 상품 좋아요 수 반환
  deactivate L
  PF->>U: 사용자 정보 확인
  activate U
  U-->>PF: 로그인 정보 반환
  deactivate U
  opt 로그인한 사용자
    PF->>L: 상품 좋아요 여부 조회
    activate L
    L-->>PF: 상품 좋아요 여부 반환
  end
  deactivate L
  PF->>A: 상품 상세 정보 반환
  deactivate PF
```

# 브랜드 조회

```mermaid
sequenceDiagram
  Actor A as Actor
  Participant BF as BrandFacade
  Participant B as Brand
  
  A->>BF: 브랜드 상세 조회 요청
  activate BF
  BF->>B: 브랜드 정보 요청
  activate B
  opt 존재하지 않는 브랜드
    B-->>A: 404 Not Found
  end
  B -->> BF: 브랜드 정보 반환
  deactivate B
  BF -->> A: 브랜드 정보 응답 
  deactivate BF
  
```

# 상품 좋아요 등록/취소

## 좋아요 등록

```mermaid
sequenceDiagram
  Actor A as Actor
  Participant LF as LikeFacade
  Participant U as User
  Participant L as ProductLike
  
    A->>LF: 좋아요 등록 요청
    activate LF
    LF->>U: 사용자 정보 확인
    activate U
    opt 미로그인 / 유효하지 않은 사용자
      U-->>A: 401 Unauthroized
    end
    U-->>LF: 사용자 정보 반환
    deactivate U
    LF->>L: 좋아요 생성 요청
    activate L
    opt 좋아요 이미 존재
      Note right of L: 좋아요가 이미 존재하면,<br> 아무 작업도 하지 않음 (멱등성 보장)
      L-->>LF: 기존 좋아요 반환
    end
    L-->>LF: 생성된 좋아요 반환
    deactivate L
    LF-->>A: 좋아요 등록 응답
    deactivate LF
  
```

## 좋아요 취소

```mermaid
sequenceDiagram
  Actor A as Actor
  Participant LF as LikeFacade
  Participant U as User
  Participant L as ProductLike
    
    A->>LF:좋아요 취소 요청
    activate LF
    LF-->>U: 사용자 정보 확인
    activate U
    opt 미로그인 / 유효하지 않은 사용자
      U-->>A: 401 Unauthroized
    end
    U-->>LF: 사용자 정보 반환
    deactivate U
    LF->>L: 좋아요 삭제 요청
    activate L
    opt 좋아요 미존재
       Note right of L: 좋아요가 없으면,<br> 아무 작업도 하지 않음 (멱등성 보장)
    end
    L-->>LF: 좋아요 삭제 완료
    deactivate L
    LF-->>A: 좋아요 취소 완료
    deactivate LF
```

# 내가 좋아요한 상품 목록 조회

```mermaid
sequenceDiagram
  Actor A as Actor
  Participant PF as ProductFacade
  Participant U as User
  Participant L as ProductLike
  Participant P as Product

  opt 페이지 사이즈 초과
  Note right of A: Controller 내 페이지 유효성 검사
  A->>A: 400 Bad Request
  end
  A->>PF: 좋아요한 상품 목록 요청
  activate PF
  PF->>U: 사용자 정보 확인
  activate U
  opt 미로그인 / 유효하지 않은 사용자
    U-->>A: 401 Unauthorized
  end
    U-->>PF: 사용자 정보 반환
    deactivate U
    PF->>L: 페이지에 해당하는 좋아요 조회
    activate L
    L-->>PF: 페이지에 해당하는 좋아요 반환
    deactivate L
    PF->>P: 좋아요한 상품 정보 조회
    activate P
    P-->>PF: 좋아요한 상품 정보 반환
    deactivate P
    PF-->>A: 좋아요한 상품 목록 반환
    deactivate PF
```

# 주문 생성 API

```mermaid
sequenceDiagram
  Actor A as Actor
  Participant OF as OrderFacade
  Participant U as User
  Participant Pd as Product
  Participant O as Order
  Participant C as Coupon
  participant E as Event
  
  A->>OF: 주문 요청
  activate OF
  OF->>U: 사용자 정보 확인
  activate U
  
  opt 미로그인 / 유효하지 않은 사용자
    U-->>A: 401 Unauthorized
  end
  U-->>OF: 사용자 정보 반환
  deactivate U
  OF->>Pd: 주문 상품 조회 요청
  activate Pd
  opt 상품 미존재
    Pd-->>A: 404 Not Found 
  end
  opt 상품 구매 불가
    Pd-->>A: 409 Conflict
  end
  Pd-->>OF: 상품 정보 반환
  deactivate Pd
  opt 쿠폰 사용
    OF->>C: 쿠폰 조회
    activate C
    C-->>OF: 쿠폰 정보 반환
    deactivate C
  end
  OF->>O: 주문 생성
  activate O
  O-->>OF: 주문 반환
  deactivate O
  OF->>E: 주문 생성 이벤트 발행
    OF-->>A: 주문 생성 응답
    deactivate OF
    
  
```

# 결제 시도 요청 API
```mermaid
sequenceDiagram
    Actor A as Actor
    participant PF as PaymentFacade
    participant O as Order
    participant P as Payment
    participant PG as PaymentGateway
    A->>PF: 결제 시도 요청
    activate PF
    PF->>O: 주문 정보 조회
    opt 존재하지 않는 주문
      O-->>A: 404 Not Found
    end
    PF->>P: 결제 시도 요청
    activate P
    P->>PG: 결제 요청
    activate PG
    alt 결제 실패
        PG-->>P: 결제 요청 실패 응답
    else 결제 요청 성공
        PG-->>P: 결제 요청 성공 응답 
    end
    deactivate PG
    P-->>PF: 결제 시도 결과 응답
    deactivate P
    PF-->>A: 결제 시도 결과 응답
    deactivate PF
```

# 결제 콜백 or 스케쥴러
```mermaid
sequenceDiagram
    participant C as Callback or Scheduler
    participant P as Payment
    participant E as Event
    
    C->>P: 결제 결과 처리 요청
    activate P
    alt 결제 성공
        P->>E: 결제 성공 이벤트 발행
        P-->>C: 결제 성공 처리 완료
    else 결제 실패
        P->>E: 결제 실패 이벤트 발행
        P-->>C: 결제 실패 처리 완료
    end
    
```

# 유저의 주문 목록 조회

```mermaid
sequenceDiagram
  Actor A as Actor
  Participant OF as OrderFacade
  Participant U as User
  Participant O as Order

  opt 페이지 사이즈 초과
  Note right of A: Controller 내 페이지 유효성 검사
  A->>A: 400 Bad Request
  end
  A->>OF: 주문 목록 조회 요청
  activate OF
  
  OF->>U: 사용자 정보 확인
  activate U
  opt 미로그인 / 유효하지 않은 사용자
    U-->>A: 401 Unauthroized
  end
  U-->>OF: 사용자 정보 반환
  deactivate U
  
  OF->>O: 주문 목록 조회
  activate O
  O-->>OF: 주문 목록 반환
  deactivate O
  OF-->>A: 주문 목록 반환
  deactivate OF
  
```

# 단일 주문 상세 조회

```mermaid
sequenceDiagram
  Actor A as Actor
  participant OF as OrderFacade
  participant U as User
  participant O as Order
  participant Pay as Payment
  
  A->>OF: 주문 상세 조회 요청
  activate OF
  
  OF->>U: 사용자 정보 확인
  activate U
  alt 미로그인 / 유효하지 않은 사용자 
    U-->>A: 401 Unauthorized
  end
  U-->>OF: 사용자 정보 반환
  deactivate U
  
  OF->>O: 단일 주문 조회
  activate O
  opt 로그인 사용자의 주문 X
    O-->>A: 403 Forbidden
  end
  opt 존재하지 않는 주문
    O-->>A: 404 Not Found
  end
  O-->>OF: 단일 주문 정보 반환
  deactivate O
  
  OF->>Pay: 결제 정보 요청
  activate Pay
  Pay-->>OF: 결제 정보 반환
  deactivate Pay
  OF-->>A: 주문 상세 정보 반환
  deactivate OF
```